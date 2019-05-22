package com.github.makiftutuncu.trump.domain

import scala.concurrent.Future

trait TweetRepository {
  def searchByUserName(username: String, limit: Int): Future[Seq[Tweet]]
}
