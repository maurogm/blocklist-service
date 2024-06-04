package services

import cats.Monad
import cats.implicits._
import models.{BlocklistAlg, SoTFetcherAlg}

/** Service to keep the blocklist in sync with the source of truth (SoT). */
class SynchronizerService[F[_] : Monad](blocklist: BlocklistAlg[F], sotFetcher: SoTFetcherAlg[F]) {
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
      case (Some(soTId), None) => update(soTId)
      case (Some(soTId), Some(blocklistId)) if soTId != blocklistId => update(soTId)
      case _ => Monad[F].unit
    }
  } yield ()

  private def update(newIdentifier: String): F[Unit] = for {
    newIPList <- sotFetcher.fetchRemoteList
    _ <- blocklist.updateBlocklist(newIPList, newIdentifier)
  } yield ()
}
