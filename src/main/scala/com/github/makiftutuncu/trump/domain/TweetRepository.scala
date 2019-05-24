package com.github.makiftutuncu.scalacandidatetest.domain

import com.github.makiftutuncu.scalacandidatetest.domain.models.{MaybeF, Tweet}

trait TweetRepository {
  def getTweets(username: String, limit: Int): MaybeF[List[Tweet]]
}
