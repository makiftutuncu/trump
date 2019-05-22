package com.github.makiftutuncu.trump

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.makiftutuncu.trump.application.ShoutController
import com.github.makiftutuncu.trump.infrastructure.TwitterApi

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object Starter {
  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  implicit val materializer: ActorMaterializer    = ActorMaterializer()(actorSystem)

  val tweetRepository = new TwitterApi()
  val shoutController = new ShoutController(tweetRepository)

  def main(args: Array[String]): Unit =
    Await.result(
      Http().bindAndHandle(shoutController.route, "0.0.0.0", 9000),
      Duration.Inf
    )
}
