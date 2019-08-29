package com.github.makiftutuncu.trump.domain

import com.github.makiftutuncu.trump.domain.models.{MaybeF, Tweet}

trait TweetRepository {
  def getTweets(username: String, limit: Int): MaybeF[List[Tweet]]
}
