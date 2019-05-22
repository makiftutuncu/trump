package com.github.makiftutuncu.trump

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.makiftutuncu.trump.application.ShoutController
import com.github.makiftutuncu.trump.infrastructure.TweetRepositoryInMemory

object Starter {
  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val materializer: ActorMaterializer    = ActorMaterializer()(actorSystem)
  implicit val executionContext: ExecutionContext = ExecutionContext.global

  val tweetRepository = new TweetRepositoryInMemory()
  val shoutController = new ShoutController(tweetRepository)

  def main(args: Array[String]): Unit =
    Await.result(
      Http().bindAndHandle(shoutController.route, "0.0.0.0", 9000),
      Duration.Inf
    )
}
