package config

import pureconfig._
import pureconfig.generic.auto._

case class SourceConfig(url: String)
case class RedisConfig(uri:String, port:String)

case class AppConfig(source: SourceConfig, redis: RedisConfig)

object ConfigLoader {
  private def loadSourceConfig: SourceConfig = ConfigSource.default.at("remoteData").loadOrThrow[SourceConfig]
  private def loadRedisConfig: RedisConfig = ConfigSource.default.at("redis").loadOrThrow[RedisConfig]

  def loadConfig: AppConfig = AppConfig(loadSourceConfig, loadRedisConfig)
}
