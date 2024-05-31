package http

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import services.BlocklistService

class BlocklistRoutes[F[_] : Sync](blocklistService: BlocklistService[F]) extends Http4sDsl[F] {

  lazy val helpMessage =
    """
      |Available endpoints:
      | - GET /ips/{ip} : Check if the given IP is blocked
      | - GET /health  : Health check
        """.stripMargin

  def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "ips" / ip =>
      for {
        isBlocked <- blocklistService.checkIp(ip)
        response <- Ok(isBlocked.toString)
      } yield response

    case GET -> Root / "health" =>
      Ok("Service is up and running")

    case GET -> Root => Ok(helpMessage)
    case GET -> Root / "ips" => Ok(helpMessage)

  }

}
