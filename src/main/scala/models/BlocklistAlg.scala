package models

trait BlocklistAlg[F[_]] {
  def isBlocked(ip: String): F[Boolean]
  def updateBlocklist: F[Unit]
}
