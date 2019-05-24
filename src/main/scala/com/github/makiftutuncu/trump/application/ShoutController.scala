package com.github.makiftutuncu.trump.application

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{complete, get, handleExceptions, path, _}
import akka.http.scaladsl.server.Route
import com.github.makiftutuncu.trump.domain._

import scala.concurrent.ExecutionContext

class ShoutController(val shoutService: ShoutService)(implicit as: ActorSystem, ec: ExecutionContext) extends Controller {
  override val route: Route =
    get {
      path("shout" / Segment) { twitterUserName =>
        handleExceptions(errorHandler) {
          parameters("limit".as[Int]) { limit =>
            onSuccess(shoutService.shoutForUser(twitterUserName, limit)) {
              case Left(error)   => failWithError(error)
              case Right(result) => complete(result)
            }
          }
        }
      }
    }
}
