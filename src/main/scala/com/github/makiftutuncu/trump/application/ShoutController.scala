package com.github.makiftutuncu.trump.application

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{complete, get, handleExceptions, path, _}
import akka.http.scaladsl.server.Route
import com.github.makiftutuncu.trump.domain._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class ShoutController(val cache: Cache[List[Tweet]],
                      val limitValidator: LimitValidator,
                      val tweetRepository: TweetRepository)(implicit as: ActorSystem,
                                                                     ec: ExecutionContext) extends Controller {
  override val route: Route =
    get {
      path("shout" / Segment) { twitterUserName =>
        handleExceptions(errorHandler) {
          parameters("limit".as[Int]) { limit =>
            shoutForUser(twitterUserName, limit)
          }
        }
      }
    }

  private def shoutForUser(twitterUserName: String, limit: Int): Route = {
    logger.debug(s"Going to try and shout $limit tweets of user $twitterUserName")

    limitValidator.validate(limit) match {
      case Some(error) =>
        logger.error(s"Failed to shout tweets of user $twitterUserName because $error")
        failWithError(error)

      case None =>
        val result = cache.use(twitterUserName)(tweetRepository.getTweets(twitterUserName, limit)) { tweets =>
          MaybeF.value(tweets.asJson)
        }

        onSuccess(result) {
          case Left(error)   => failWithError(error)
          case Right(tweets) => complete(tweets)
        }
    }
  }
}
