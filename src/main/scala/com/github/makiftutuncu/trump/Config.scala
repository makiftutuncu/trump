package com.github.makiftutuncu.trump

import com.github.makiftutuncu.trump.Config.{Limits, Server, Twitter}
import com.typesafe.config.{Config => TypesafeConfig}

case class Config(limits: Limits, server: Server, twitter: Twitter)

object Config {
  def apply(config: TypesafeConfig): Config =
    Config(
      Limits(config.getConfig("limits")),
      Server(config.getConfig("server")),
      Twitter(config.getConfig("twitter"))
    )

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

  case class Twitter(apiKey: String, apiSecret: String, mock: Boolean)

  object Twitter {
    def apply(config: TypesafeConfig): Twitter =
      Twitter(
        config.getString("apiKey"),
        config.getString("apiSecret"),
        config.getBoolean("mock")
      )
  }
}
