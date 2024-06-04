import cats.effect.{Async, Sync}
import cats.syntax.all._
import models.IPv4Validation.{IPv4Address, validateIPv4Address}
import models.SoTFetcherAlg
import org.http4s.client.Client
import org.http4s.headers.ETag
import org.http4s.implicits._
import org.http4s.{Method, Request, Uri}


class SoTFetcherImpl[F[_] : Async](client: Client[F], url: Uri) extends SoTFetcherAlg[F] {
  override def getSourceIdentifier: F[Option[String]] = {
    val request = Request[F](Method.HEAD, url)
    client.run(request).use { response =>
      Sync[F].pure(response.headers.get[ETag].map(_.value))
    }
  }

  override def fetchRemoteList: F[List[IPv4Address]] = {
    val request = Request[F](Method.GET, url)
    client.run(request).use { response =>
      for {
        rawStrResponse <- response.as[String] //read raw as string
        strList = rawStrResponse.split("\n").toList //break lines and make a list
        maybeAddresses = strList.map(s => validateIPv4Address(s)) //validate IPv4 format
        validIPs = maybeAddresses.collect { case Right(value) => value } //collect only lines that are valid IPs
      } yield validIPs
    }
  }
}
