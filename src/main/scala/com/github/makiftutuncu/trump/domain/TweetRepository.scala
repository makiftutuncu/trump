package com.github.makiftutuncu.trump.domain

import scala.concurrent.Future

trait TweetRepository {
  def getTweets(username: String, limit: Int): Future[Either[ShoutError, List[Tweet]]]
}
