package com.github.makiftutuncu.trump

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.makiftutuncu.trump.domain.{Cache, LimitValidator, Tweet, TweetRepository}
import com.github.makiftutuncu.trump.infrastructure.{MockTwitterApi, Redis, TwitterApi}
import com.redis.RedisClient
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

trait Components {
  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  implicit val materializer: ActorMaterializer    = ActorMaterializer()(actorSystem)

  lazy val config = Config(ConfigFactory.load())

  lazy val redis: RedisClient = new RedisClient(config.cache.host, config.cache.port)

  lazy val accessTokenCache: Cache[String] = new Redis[String](config.cache, redis)
  lazy val tweetCache: Cache[List[Tweet]]  = new Redis[List[Tweet]](config.cache, redis)(executionContext, Redis.tweetParse)

  lazy val limitValidator = new LimitValidator(config.limits)

  lazy val tweetRepository: TweetRepository =
    if (config.twitter.mock) {
      new MockTwitterApi()
    } else {
      new TwitterApi(accessTokenCache, config.twitter)
    }
}