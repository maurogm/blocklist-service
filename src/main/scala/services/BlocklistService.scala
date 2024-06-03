package services

import cats.effect.Sync
import cats.implicits._
import models.BlocklistAlg
import models.IPv4Validation.{IPv4Address, validateIPv4Address}

class BlocklistService[F[_] : Sync](blocklist: BlocklistAlg[F]) {
  /**
   * Checks if the provided IP address is blocked.
   *
   * @param unsafeStrIP the IP address as a raw string, which needs to be validated.
   * @return a `F[Either[String, Boolean]]` where:
   *         - `Right(true)` indicates the IP address is blocked.
   *         - `Right(false)` indicates the IP address is not blocked.
   *         - `Left(error)` indicates the IP address is invalid, with the error message describing the validation failure.
   */
  def checkIp(unsafeStrIP: String): F[Either[String, Boolean]] = {
    val validatedIP: Either[String, IPv4Address] = validateIPv4Address(unsafeStrIP)
    validatedIP.traverse(blocklist.isBlocked)
  }

  def updateBlocklist: F[Unit] = ???
}
