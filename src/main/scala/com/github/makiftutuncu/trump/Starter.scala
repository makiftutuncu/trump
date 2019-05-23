package com.github.makiftutuncu.trump

import akka.http.scaladsl.Http
import com.github.makiftutuncu.trump.application.ShoutController

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Starter extends Components {
  val shoutController = new ShoutController(limitValidator, tweetRepository)

  def main(args: Array[String]): Unit =
    Await.result(
      Http().bindAndHandle(shoutController.route, config.server.host, config.server.port),
      Duration.Inf
    )
}
