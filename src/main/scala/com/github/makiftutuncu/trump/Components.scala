package com.github.makiftutuncu.trump

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.makiftutuncu.trump.domain.models.Tweet
import com.github.makiftutuncu.trump.domain.{Cache, HttpClient, TweetRepository}
import com.github.makiftutuncu.trump.infrastructure._
import com.redis.RedisClient
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

trait Components {
  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  implicit val materializer: ActorMaterializer    = ActorMaterializer()(actorSystem)

  lazy val config: Config = Config(ConfigFactory.load())

  lazy val redis: RedisClient = new RedisClient(config.redis.host, config.redis.port)

  lazy val accessTokenCache: Cache[String] = new Redis[String](config.redis, () => redis)
  lazy val tweetCache: Cache[List[Tweet]]  = new Redis[List[Tweet]](config.redis, () => redis)(executionContext, Redis.tweetParse)

  lazy val limitValidator: LimitValidator = new LimitValidator(config.limits)

  lazy val akkaHttpClient: HttpClient = new AkkaHttpClient()

  lazy val tweetRepository: TweetRepository =
    if (config.twitter.mock) {
      new MockTwitterApi()
    } else {
      new TwitterApi(accessTokenCache, config.twitter, akkaHttpClient)
    }

  lazy val shoutService: ShoutService = new ShoutService(tweetCache, limitValidator, tweetRepository)
}
