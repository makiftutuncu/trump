package com.github.makiftutuncu.trump.domain

import com.github.makiftutuncu.trump.Components
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
