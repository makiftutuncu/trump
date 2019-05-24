package com.github.makiftutuncu.scalacandidatetest

import akka.http.scaladsl.Http
import com.github.makiftutuncu.scalacandidatetest.application.ShoutController
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Starter extends Components with StrictLogging {
  val shoutController = new ShoutController(shoutService)

  def main(args: Array[String]): Unit = {
    val server = Http().bindAndHandle(shoutController.route, config.server.host, config.server.port)
    logger.info(s"Server is running on ${config.server.host}:${config.server.port}!")
    Await.result(server, Duration.Inf)
  }
}
