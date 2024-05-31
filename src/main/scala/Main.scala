import cats.effect.{ExitCode, IO, IOApp}
import http.BlocklistRoutes
import interpreters.DummyBlocklistInterpreter
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import services.BlocklistService


object Main extends IOApp {
  import scala.concurrent.ExecutionContext.global

  def run(args: List[String]): IO[ExitCode] = {
    for {
      blocklist <- DummyBlocklistInterpreter[IO]
      blocklistService = new BlocklistService(blocklist)
      httpApp = new BlocklistRoutes(blocklistService).routes.orNotFound
      exitCode <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "localhost")
        .withHttpApp(httpApp)
        .resource
        .useForever
        .as(ExitCode.Success)
    } yield exitCode
  }
}