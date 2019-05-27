package com.github.makiftutuncu.scalacandidatetest.infrastructure

import com.github.makiftutuncu.scalacandidatetest.domain.models.{Errors, Maybe}
import com.github.makiftutuncu.scalacandidatetest.{Components, MockCache, MockHttpClient}
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ShoutServiceSpec extends WordSpec with MustMatchers with Components {
  "Shouting tweets" must {
    "fail when limit is invalid" in {
      Await.result(shoutService.shoutForUser("test", 42), Duration.Inf) must be(Maybe.error(Errors.invalidLimit(42, limitValidator.limits.min, limitValidator.limits.max)))
    }

    "fail when getting tweets fails" in {
      val service = new ShoutService(tweetCache, limitValidator, new TwitterApi(new MockCache(), config.twitter, new MockHttpClient()))
      Await.result(service.shoutForUser("test", 3), Duration.Inf) must be(Maybe.error(Errors.twitterConnection("Cannot get access token!")))
    }

    "shout some tweets properly" in {
      val maybeTweets = Await.result(shoutService.shoutForUser("test", 3).map(_.flatMap(_.as[List[String]])), Duration.Inf)

      maybeTweets.isRight must be(true)

      val tweets = maybeTweets.right.get

      tweets must have(size(3))

      tweets.foreach { tweet =>
        tweet               must be(tweet.toUpperCase)
        tweet.endsWith("!") must be(true)
      }
    }
  }
}
