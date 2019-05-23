package com.github.makiftutuncu.trump

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.makiftutuncu.trump.domain.{LimitValidator, TweetRepository}
import com.github.makiftutuncu.trump.infrastructure.{MockTwitterApi, TwitterApi}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

trait Components {
  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  implicit val materializer: ActorMaterializer    = ActorMaterializer()(actorSystem)

  lazy val config = Config(ConfigFactory.load())

  lazy val limitValidator = new LimitValidator(config.limits)

  lazy val tweetRepository: TweetRepository =
    if (config.twitter.mock) {
      new MockTwitterApi()
    } else {
      new TwitterApi(config.twitter)
    }
}
