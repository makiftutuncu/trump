package com.github.makiftutuncu.trump.application

import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.Directives.{complete, get, handleExceptions, path, _}

class ShoutController {

  // feel free to change error handler to return errors in a more RESTful way
  private val errorHandler = ExceptionHandler {
    case _ => complete("error happened")
  }

  val route: Route = get {
    path("shout" / Segment) { twitterUserName =>
      parameters('limit.as[Int]) { limit =>
        handleExceptions(errorHandler) {
          complete(s"HELLO ${twitterUserName.toUpperCase()} !")
        }
      }
    }
  }
}
