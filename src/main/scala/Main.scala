import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.syntax.all._
import config.{AppConfig, ConfigLoader}
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import http.BlocklistRoutes
import interpreters.{RedisBlocklistInterpreter, SoTFetcherInterpreter}
import models.BlocklistAlg
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, Uri}
import services.{BlocklistService, SynchronizerService}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

object Main extends IOApp {
  private val appConfig: AppConfig = ConfigLoader.loadConfig
  private val sourceUrl = Uri.unsafeFromString(appConfig.source.url)
  private val redisUri = appConfig.redis.fullUri

  def run(args: List[String]): IO[ExitCode] = {

    val clientResource: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](global)
      .withRequestTimeout(5.seconds)
      .resource

    val blocklistResource: Resource[IO, BlocklistAlg[IO]] = RedisBlocklistInterpreter.make[IO](redisUri)

    blocklistResource.use { blocklist =>
      val blocklistService = new BlocklistService(blocklist)
      val sotFetcher = new SoTFetcherInterpreter[IO](clientResource, sourceUrl)
      val synchronizerService = new SynchronizerService(blocklist, sotFetcher)
      val httpApp = new BlocklistRoutes(blocklistService).routes.orNotFound
      (runServer(httpApp), runSynchronizer(synchronizerService)).parMapN((_, _) => ExitCode.Success)
    }
  }

  private def runServer(httpApp: HttpApp[IO]): IO[Unit] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .resource
      .useForever

  private def runSynchronizer(synchronizerService: SynchronizerService[IO]): IO[Unit] = {
    val secondsRefresh: Int = appConfig.synchronizer.secondsRefresh
    val sleepDuration: FiniteDuration = secondsRefresh.seconds

    def loop: IO[Unit] = {
      IO(println("Por sincronizar")) >> synchronizerService.synchronize >> IO(println("Sincronizado!")) >> Temporal[IO].sleep(sleepDuration) >> loop
    }

    loop
  }
}
