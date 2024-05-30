package interpreters

import cats.Applicative
import models.BlocklistAlg

class DummyBlocklistInterpreter[F[_]: Applicative] extends BlocklistAlg[F] {
  // Simulates that the only  blocked IP is "127.0.0.1"
  override def isBlocked(ip: String): F[Boolean] = {
    Applicative[F].pure {
      ip == "127.0.0.1"
    }
  }

  //Simulates an update by doing nothing
  override def updateBlocklist: F[Unit] = {
    Applicative[F].unit
  }
}

object DummyBlocklistInterpreter {
  def apply[F[_]: Applicative]: F[BlocklistAlg[F]] =
    Applicative[F].pure(new DummyBlocklistInterpreter[F])
}
