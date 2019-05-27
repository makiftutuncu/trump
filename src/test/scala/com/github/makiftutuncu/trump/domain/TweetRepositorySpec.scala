package com.github.makiftutuncu.scalacandidatetest.domain

import com.github.makiftutuncu.scalacandidatetest.Components
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TweetRepositorySpec extends WordSpec with MustMatchers with Components {
  "Getting tweets" must {
    "return some tweets" in {
      val tweets = Await.result(tweetRepository.getTweets("test", 3), Duration.Inf)
      tweets.isRight   must be(true)
      tweets.right.get must have(size(3))
    }
  }
}
