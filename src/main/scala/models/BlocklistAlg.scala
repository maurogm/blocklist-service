package models

import models.IPv4.IPv4Address

trait BlocklistAlg[F[_]] {
  def isBlocked(ip: IPv4Address): F[Boolean]
  def updateBlocklist: F[Unit]
}
