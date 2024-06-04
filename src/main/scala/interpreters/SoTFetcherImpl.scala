package interpreters

import cats.effect.{Async, Resource, Sync}
import cats.syntax.all._
import models.IPv4Validation.{IPv4Address, validateIPv4Address}
import models.SoTFetcherAlg
import org.http4s.client.Client
import org.http4s.headers.ETag
import org.http4s.implicits._
import org.http4s.{Method, Request, Uri}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class SoTFetcherImpl[F[_] : Async](clientResource: Resource[F, Client[F]], url: Uri) extends SoTFetcherAlg[F] {
  implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  override def getSourceIdentifier: F[Option[String]] = {
    val request = Request[F](Method.HEAD, url)
    clientResource.use { client =>
      client.run(request).use { response =>
        val maybeETag: Option[String] = response.headers.get[ETag].map(_.value)
        Sync[F].pure(maybeETag)
      }.handleErrorWith { error =>
        logger.error(error)(s"Error fetching source identifier: $error") *> Sync[F].pure(None)
      }
    }
  }

  override def fetchRemoteList: F[Option[List[IPv4Address]]] = {
    val request = Request[F](Method.GET, url)
    clientResource.use { client =>
      client.run(request).use { response =>
        for {
          rawStrResponse <- response.as[String]
          strList = rawStrResponse.split("\n").toList
          maybeAddresses = strList.map(validateIPv4Address)
          validIPs = maybeAddresses.collect { case Right(value) => value }
        } yield Option(validIPs)
      }
    }.handleErrorWith { error =>
      logger.error(error)(s"Error fetching remote list: $error") *> Sync[F].pure(None)
    }
  }
}
