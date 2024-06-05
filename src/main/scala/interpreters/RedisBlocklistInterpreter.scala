package interpreters

import cats.effect.{Async, Resource}
import cats.implicits._
import dev.profunktor.redis4cats.connection._
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import models.BlocklistAlg
import models.IPv4Validation.IPv4Address

class RedisBlocklistInterpreter[F[_] : Async : Log](redis: RedisCommands[F, String, String]) extends BlocklistAlg[F] {

  private val identifierKey = "blocklist:identifier"
  private val ipsSetKey = "blocklist:ips"

  override def identifier: F[Option[String]] = redis.get(identifierKey)

  override def isBlocked(ip: IPv4Address): F[Boolean] =
    redis.sIsMember(ipsSetKey, ip.value.value)

  override def updateBlocklist(ips: Set[IPv4Address], newIdentifier: String): F[Unit] = for {
    currentIps <- redis.sMembers(ipsSetKey).map(_.toSet)
    newIps = ips.map(_.value.value)
    ipsToRemove = currentIps.diff(newIps)

    _ <- if (ipsToRemove.nonEmpty) redis.sRem(ipsSetKey, ipsToRemove.toSeq: _*) else Async[F].unit // Remove IPs no longer blocked
    _ <- redis.sAdd(ipsSetKey, newIps.toSeq: _*) // Let Redis handle duplicates
    _ <- redis.set(identifierKey, newIdentifier) // Update the identifier of this
  } yield ()
}

object RedisBlocklistInterpreter {
  def make[F[_] : Async : Log](redisUri: String): Resource[F, BlocklistAlg[F]] =
    for {
      redisClient <- RedisClient[F].from(redisUri)
      redis <- Redis[F].fromClient(redisClient, RedisCodec.Utf8)
    } yield new RedisBlocklistInterpreter(redis)
}
