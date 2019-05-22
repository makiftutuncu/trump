package com.github.makiftutuncu.trump.application

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity.{ChunkStreamPart, Chunked}
import akka.http.scaladsl.model.{ContentTypes, HttpResponse}
import akka.http.scaladsl.server.Directives.{complete, get, handleExceptions, path, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.scaladsl.Source
import com.github.makiftutuncu.trump.domain.{Errors, LimitValidator, ShoutError, TweetRepository}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class ShoutController(val tweetRepository: TweetRepository)(implicit as: ActorSystem, ec: ExecutionContext) extends FailFastCirceSupport {
  private val errorHandler =
    ExceptionHandler {
      case _ => failWithError(Errors.unknown)
    }

  val route: Route = get {
    handleExceptions(errorHandler) {
      path("shout" / Segment) { twitterUserName =>
        parameters("limit".as[Int]) { limit =>
          shoutForUser(twitterUserName, limit)
        }
      }
    }
  }

  private def shoutForUser(twitterUserName: String, limit: Int): Route =
    LimitValidator.validate(limit) match {
      case Some(error) =>
        failWithError(error)

      case None =>
        onSuccess(tweetRepository.getTweets(twitterUserName, limit)) {
          case Left(error)   => failWithError(error)
          case Right(tweets) => complete(tweets.map(_.asJson))
        }
    }

  private def failWithError(error: ShoutError): Route =
    complete {
      HttpResponse(
        error.code,
        entity = Chunked(
          ContentTypes.`application/json`,
          Source.single(error.asJson.noSpaces).map(ChunkStreamPart.apply)
        )
      )
    }
}
