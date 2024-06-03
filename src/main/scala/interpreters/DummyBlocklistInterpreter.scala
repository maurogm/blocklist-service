package interpreters

import cats.Applicative
import models.BlocklistAlg
import models.IPv4Validation.IPv4Address

class DummyBlocklistInterpreter[F[_]: Applicative] extends BlocklistAlg[F] {
  override def identifier: F[Option[String]] =
    Applicative[F].pure(Option("Dummy Blocklist"))

  // Simulates that the only  blocked IP is "127.0.0.1"
  override def isBlocked(ip: IPv4Address): F[Boolean] = {
    Applicative[F].pure {
      ip.value.toString == "127.0.0.1"
    }
  }

  // Simulates an update by doing nothing
  override def updateBlocklist(ips: List[IPv4Address]): F[Unit] = {
    Applicative[F].unit
  }
}

object DummyBlocklistInterpreter {
  def apply[F[_]: Applicative]: F[BlocklistAlg[F]] =
    Applicative[F].pure(new DummyBlocklistInterpreter[F])
}