package com.github.makiftutuncu.scalacandidatetest

import com.github.makiftutuncu.scalacandidatetest.Config.{Cache, Limits, Server, Twitter}
import com.typesafe.config.{Config => TypesafeConfig}

case class Config(cache: Cache, limits: Limits, server: Server, twitter: Twitter)

object Config {
  def apply(config: TypesafeConfig): Config =
    Config(
      Cache(config.getConfig("cache")),
      Limits(config.getConfig("limits")),
      Server(config.getConfig("server")),
      Twitter(config.getConfig("twitter"))
    )

  case class Cache(enabled: Boolean, host: String, port: Int, ttl: Int)

  object Cache {
    def apply(config: TypesafeConfig): Cache =
      Cache(
        config.getBoolean("enabled"),
        config.getString("host"),
        config.getInt("port"),
        config.getInt("ttl")
      )
  }

  case class Limits(min: Int, max: Int)

  object Limits {
    def apply(config: TypesafeConfig): Limits =
      Limits(
        config.getInt("min"),
        config.getInt("max")
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
