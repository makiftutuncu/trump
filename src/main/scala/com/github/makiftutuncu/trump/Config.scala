package com.github.makiftutuncu.trump

import com.github.makiftutuncu.trump.Config.{Redis, Limits, Server, Twitter}
import com.typesafe.config.{Config => TypesafeConfig}

case class Config(limits: Limits, redis: Redis, server: Server, twitter: Twitter)

object Config {
  case class Limits(min: Int, max: Int)

  object Limits {
    def apply(config: TypesafeConfig): Limits =
      Limits(
        config.getInt("min"),
        config.getInt("max")
      )
  }

  def apply(config: TypesafeConfig): Config =
    Config(
      Limits(config.getConfig("limits")),
      Redis(config.getConfig("redis")),
      Server(config.getConfig("server")),
      Twitter(config.getConfig("twitter"))
    )

  case class Redis(enabled: Boolean, host: String, port: Int, ttl: Int)

  object Redis {
    def apply(config: TypesafeConfig): Redis =
      Redis(
        config.getBoolean("enabled"),
        config.getString("host"),
        config.getInt("port"),
        config.getInt("ttl")
      )
  }

  case class Server(host: String, port: Int)

  object Server {
    def apply(config: TypesafeConfig): Server =
      Server(
        config.getString("host"),
        config.getInt("port")
      )
  }

  case class Twitter(accessTokenTTL: Int, apiKey: String, apiSecret: String, mock: Boolean)

  object Twitter {
    def apply(config: TypesafeConfig): Twitter =
      Twitter(
        config.getInt("accessTokenTTL"),
        config.getString("apiKey"),
        config.getString("apiSecret"),
        config.getBoolean("mock")
      )
  }
}
