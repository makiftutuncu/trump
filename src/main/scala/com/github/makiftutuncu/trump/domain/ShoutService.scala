package com.github.makiftutuncu.trump.domain

import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import io.circe.Json
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class ShoutService(val cache: Cache[List[Tweet]],
                   val limitValidator: LimitValidator,
                   val tweetRepository: TweetRepository)(implicit as: ActorSystem,
                                                                  ec: ExecutionContext) extends StrictLogging {
  def shoutForUser(twitterUserName: String, limit: Int): MaybeF[Json] = {
    logger.debug(s"Going to try and shout $limit tweets of user $twitterUserName")

    limitValidator.validate(limit) match {
      case Some(error) =>
        logger.error(s"Failed to shout tweets of user $twitterUserName because $error")
        MaybeF.error(error)

      case None =>
        cache.use(twitterUserName)(tweetRepository.getTweets(twitterUserName, limitValidator.limits.max)) { tweets =>
          MaybeF.value(tweets.take(limit).asJson)
        }
    }
  }
}
