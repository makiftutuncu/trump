package com.github.makiftutuncu.trump.infrastructure

import com.github.makiftutuncu.trump.Config.{Redis => RedisConfig}
import com.github.makiftutuncu.trump.domain.Cache
import com.github.makiftutuncu.trump.domain.models.{MaybeF, Tweet}
import com.redis.serialization.Parse
import com.redis.{RedisClient, Seconds}
import io.circe.parser.parse

import scala.concurrent.ExecutionContext

class Redis[A](val config: RedisConfig, val redis: () => RedisClient)(implicit ec: ExecutionContext, p: Parse[A]) extends Cache[A] {
  override val isEnabled: Boolean       = config.enabled
  override val defaultTTLInSeconds: Int = config.ttl

  override protected def internalGet(key: String): MaybeF[Option[A]] =
    MaybeF.from {
      logger.debug(s"Getting key $key from Redis")
      redis().get[A](key)
    }

  override protected def internalSet(key: String, value: String, ttl: Int): MaybeF[Unit] =
    MaybeF.from {
      logger.debug(s"Setting key $key to Redis")
      redis().set(key, value, onlyIfExists = false, Seconds(ttl))
    }
}

object Redis {
  val tweetParse: Parse[List[Tweet]] = Parse[List[Tweet]] { bytes =>
    parse(new String(bytes, "UTF-8")).flatMap(_.as[List[String]]).map(_.map(Tweet.apply)).getOrElse(throw new Exception("Cannot parse as list of tweets"))
  }
}
