package com.github.makiftutuncu.trump.domain

trait TweetRepository {
  def getTweets(username: String, limit: Int): MaybeF[List[Tweet]]
}
