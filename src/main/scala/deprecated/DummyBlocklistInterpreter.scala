package deprecated

import cats.Applicative
import models.BlocklistAlg
import models.IPv4Validation.IPv4Address

/**
 * Initial implementation of a BlocklistAlg, to allow for a quick proof of concept of the initial architecture.
 *
 * It does not implement actual business logic.
 *
 * Should be removed, but is kept in case it comes up during the interview.
 */
class DummyBlocklistInterpreter[F[_] : Applicative] extends BlocklistAlg[F] {
  override def identifier: F[Option[String]] =
    Applicative[F].pure(Option("Dummy Blocklist"))

  // Simulates that the only  blocked IP is "127.0.0.1"
  override def isBlocked(ip: IPv4Address): F[Boolean] = {
    Applicative[F].pure {
      ip.value.toString == "127.0.0.1"
    }
  }

  // Simulates an update by doing nothing
  override def updateBlocklist(ips: Set[IPv4Address], newIdentifier: String): F[Unit] = {
    Applicative[F].unit
  }
}

object DummyBlocklistInterpreter {
  def apply[F[_] : Applicative]: F[BlocklistAlg[F]] =
    Applicative[F].pure(new DummyBlocklistInterpreter[F])
}