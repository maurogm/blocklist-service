package models

import models.IPv4Validation.IPv4Address

trait BlocklistAlg[F[_]] {
  def identifier: F[Option[String]]
  def isBlocked(ip: IPv4Address): F[Boolean]
  def updateBlocklist(ips: Set[IPv4Address], newIdentifier: String): F[Unit]
}

trait SoTFetcherAlg[F[_]] {
  def getSourceIdentifier: F[Option[String]]
  def fetchRemoteList: F[Option[Set[IPv4Address]]]
}