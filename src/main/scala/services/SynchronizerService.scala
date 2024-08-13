package services

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import models.{BlocklistAlg, SoTFetcherAlg}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

/** Service to keep the blocklist in sync with the source of truth (SoT). */
class SynchronizerService[F[_] : Sync](blocklist: BlocklistAlg[F], sotFetcher: SoTFetcherAlg[F]) {
  implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  /**
   * Synchronizes the blocklist with the source of truth.
   *
   * Only fetches the remote data if the IDs of the local and remote blocklists differ,
   * or if the local blocklist's identifier is missing.
   *
   * If does not take action unless it can get the Source of Truth's identifier.
   */
  def synchronize: F[Unit] = for {
    maybeSoTId <- sotFetcher.getSourceIdentifier
    maybeCurrentBlocklistId <- blocklist.identifier
    _ <- (maybeSoTId, maybeCurrentBlocklistId) match {
      case (Some(soTId), None) =>
        logger.warn("Blocklist identifier not found. Proceeding to fill the blocklist regardless") >>
          update(soTId)
      case (Some(soTId), Some(blocklistId)) if soTId != blocklistId =>
        logger.info("Source of truth updated. Proceeding to synchronize local blocklist") >>
          update(soTId)
      case _ => Monad[F].unit
    }
  } yield ()

  private def update(newIdentifier: String): F[Unit] = for {
    maybeIPList <- sotFetcher.fetchRemoteList
    _ <- maybeIPList match {
    case Some(newIPList) => {
      blocklist.updateBlocklist(newIPList, newIdentifier)
    }
    case None => Monad[F].unit // If it fails to fetch the list, does nothing. Should take a better action
  }
  } yield ()
}
