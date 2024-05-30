package services

import cats.effect.Sync
import models.BlocklistAlg

class BlocklistService[F[_]: Sync](blocklist: BlocklistAlg[F]) {
  def checkIp(ip: String): F[Boolean] = blocklist.isBlocked(ip)
  def updateBlocklist: F[Unit] = blocklist.updateBlocklist
}
