package com.github.makiftutuncu.trump

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import com.github.makiftutuncu.trump.application.ShoutController

object Starter {
  def main(args: Array[String]): Unit = {

    implicit val actorSystem: ActorSystem        = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()(actorSystem)

    val shoutController = new ShoutController()

    Await.result(Http().bindAndHandle(shoutController.route, "0.0.0.0", 9000), Duration.Inf)
  }
}
