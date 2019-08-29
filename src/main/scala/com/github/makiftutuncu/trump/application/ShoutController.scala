package com.github.makiftutuncu.trump.application

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{get, handleExceptions, path, _}
import akka.http.scaladsl.server.Route
import com.github.makiftutuncu.trump.infrastructure.ShoutService

import scala.concurrent.ExecutionContext

class ShoutController(val shoutService: ShoutService)(implicit as: ActorSystem, ec: ExecutionContext) extends Controller {
  override val route: Route =
    get {
      path("shout" / Segment) { twitterUserName =>
        handleExceptions(errorHandler) {
          parameters("limit".as[Int]) { limit =>
            respond(shoutService.shoutForUser(twitterUserName, limit))
          }
        }
      }
    }
}
