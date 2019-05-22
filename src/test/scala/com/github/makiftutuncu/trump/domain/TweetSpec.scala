package com.github.makiftutuncu.trump.domain

import org.scalatest.{MustMatchers, WordSpec}

class TweetSpec extends WordSpec with MustMatchers {
  "Shouting a tweet" must {
    "return empty string when trimmed tweet is empty or it contains nothing but '!' or '.'" in {
      Tweet("").shouted       must be("")
      Tweet("  ").shouted     must be("")
      Tweet(".!.!.!").shouted must be("")
    }

    "return shouted tweet with '!' at the end" in {
      Tweet("test").shouted  must be("TEST!")
      Tweet("test.").shouted must be("TEST!")
      Tweet("test!").shouted must be("TEST!")
    }
  }
}
