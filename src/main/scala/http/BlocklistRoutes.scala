package http

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import services.BlocklistService

class BlocklistRoutes[F[_] : Sync](blocklistService: BlocklistService[F]) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "ips" / ip =>
      for {
        isBlocked <- blocklistService.checkIp(ip)
        response <- Ok(isBlocked.toString)
      } yield response
  }
}
