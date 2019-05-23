package com.github.makiftutuncu.trump

import akka.http.scaladsl.Http
import com.github.makiftutuncu.trump.application.ShoutController
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Starter extends Components with StrictLogging {
  val shoutController = new ShoutController(limitValidator, tweetRepository)

  def main(args: Array[String]): Unit = {
    val server = Http().bindAndHandle(shoutController.route, config.server.host, config.server.port)
    logger.info(s"Server is running on ${config.server.host}:${config.server.port}!")
    Await.result(server, Duration.Inf)
  }
}
