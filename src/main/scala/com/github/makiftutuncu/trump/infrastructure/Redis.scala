package com.github.makiftutuncu.scalacandidatetest.infrastructure

import com.github.makiftutuncu.scalacandidatetest.Config.{Cache => CacheConfig}
import com.github.makiftutuncu.scalacandidatetest.domain.Cache
import com.github.makiftutuncu.scalacandidatetest.domain.models.{MaybeF, Tweet}
import com.redis.serialization.Parse
import com.redis.{RedisClient, Seconds}
import com.typesafe.scalalogging.StrictLogging
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Encoder, Json}

import scala.concurrent.ExecutionContext

class Redis[A](val config: CacheConfig,
               val redis: RedisClient)(implicit ec: ExecutionContext, p: Parse[A]) extends Cache[A] with StrictLogging {
  override val defaultTTLInSeconds: Int = config.ttl

  override def get(key: String): MaybeF[Option[A]] =
    if (!config.enabled) {
      logger.debug(s"Not getting key $key from cache, cache is disabled")
      MaybeF.value(None)
    } else {
      MaybeF.from {
        logger.debug(s"Getting key $key from cache")
        redis.get[A](key)
      }
    }

  override def set(key: String, value: Json, ttl: Int = defaultTTLInSeconds): MaybeF[Unit] =
    if (!config.enabled) {
      logger.debug(s"Not setting key $key to cache, cache is disabled")
      MaybeF.value(())
    } else {
      MaybeF.from {
        logger.debug(s"Setting key $key to cache")
        redis.set(key, value.noSpaces, onlyIfExists = false, Seconds(ttl))
      }
    }

  override def use[B](key: String, ttl: Int = defaultTTLInSeconds)(getNewData: => MaybeF[A])(action: A => MaybeF[B])(implicit encoder: Encoder[A]): MaybeF[B] =
    get(key).flatMap {
      case Left(readError) =>
        logger.error(s"Failed to use cache for key $key: $readError")
        MaybeF.error(readError)

      case Right(Some(dataFromCache)) =>
        action(dataFromCache)

      case Right(None) =>
        if (config.enabled) { logger.debug(s"Missed the cache for key $key") }
        getNewData.flatMap {
          case Left(dataError) =>
            logger.error(s"Failed to get new data while using cache for key $key: $dataError")
            MaybeF.error(dataError)

          case Right(newData) =>
            set(key, newData.asJson, ttl).flatMap { _ =>
              action(newData)
            }
        }
    }

}

object Redis {
  val tweetParse: Parse[List[Tweet]] = Parse[List[Tweet]] { bytes =>
    parse(new String(bytes, "UTF-8")).flatMap(_.as[List[String]]).map(_.map(Tweet.apply)).getOrElse(throw new Exception("Cannot parse as list of tweets"))
  }
}
