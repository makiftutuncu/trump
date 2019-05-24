package com.github.makiftutuncu.scalacandidatetest.inftrastructure

import com.github.makiftutuncu.scalacandidatetest.Components
import org.scalatest.{MustMatchers, WordSpec}

class TweetRepositorySpec extends WordSpec with MustMatchers with Components {
  "Getting tweets" must {
    "return some tweets" in {
      tweetRepository.getTweets("test", 3).map { tweets =>
        tweets.isRight   must be(true)
        tweets.right.get must have(size(3))
      }
    }
  }
}
