import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.syntax.all._
import config.{AppConfig, ConfigLoader}
import http.BlocklistRoutes
import interpreters.{DummyBlocklistInterpreter, SoTFetcherInterpreter}
import org.http4s.{HttpApp, Uri}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import services.{BlocklistService, SynchronizerService}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

object Main extends IOApp {
  private val appConfig: AppConfig = ConfigLoader.loadConfig
  private val sourceUrl = Uri.unsafeFromString(appConfig.source.url)

  def run(args: List[String]): IO[ExitCode] = {

    val clientResource: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](global)
      .withRequestTimeout(5.seconds)
      .resource

    for {
      blocklist <- DummyBlocklistInterpreter[IO]
      blocklistService = new BlocklistService(blocklist)
      sotFetcher = new SoTFetcherInterpreter[IO](clientResource, sourceUrl)
      synchronizerService = new SynchronizerService(blocklist, sotFetcher)
      httpApp = new BlocklistRoutes(blocklistService).routes.orNotFound
      exitCode <- (runServer(httpApp), runSynchronizer(synchronizerService)).parMapN((_, _) => ExitCode.Success)
    } yield exitCode
  }

  def runServer(httpApp: HttpApp[IO]): IO[Unit] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .resource
      .useForever

  def runSynchronizer(synchronizerService: SynchronizerService[IO]): IO[Unit] = {
    val secondsRefresh: Int = appConfig.synchronizer.secondsRefresh
    val sleepDuration: FiniteDuration = secondsRefresh.seconds

    def loop: IO[Unit] = {
      IO(println("Por sincronizar")) >> synchronizerService.synchronize >> IO(println("Sincronizado!")) >> Temporal[IO].sleep(sleepDuration) >> loop
    }

    loop
  }
}
