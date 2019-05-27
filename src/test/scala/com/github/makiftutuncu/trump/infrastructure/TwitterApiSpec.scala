package com.github.makiftutuncu.scalacandidatetest.infrastructure

import com.github.makiftutuncu.scalacandidatetest.domain.models.{Errors, Tweet}
import com.github.makiftutuncu.scalacandidatetest.{Components, MockCache, MockHttpClient}
import io.circe.Json
import io.circe.syntax._
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TwitterApiSpec extends WordSpec with MustMatchers with Components {
  "Getting tweets" must {
    "fail when tweets cannot be parsed from the response" in {
      val api = new TwitterApi(cache, config.twitter, new MockHttpClient(invalidTweetsResponse1, invalidTweetsResponse2))

      Await.result(api.getTweets("test", 3), Duration.Inf) must be(Left(Errors.twitterConnection("Cannot parse tweets!")))
      Await.result(api.getTweets("test", 3), Duration.Inf) must be(Left(Errors.twitterConnection("Cannot parse tweets!")))
    }

    "get some tweets properly" in {
      val api = new TwitterApi(cache, config.twitter, new MockHttpClient(validTweetsResponse))

      Await.result(api.getTweets("test", 3), Duration.Inf) must be(Right(List(Tweet("Tweet 1"), Tweet("Tweet 2"), Tweet("Tweet 3"))))
    }
  }

  "Getting access token" must {
    "fail when access token cannot be parse from the response" in {
      val api = new TwitterApi(cache, config.twitter, new MockHttpClient(invalidAccessTokenResponse))

      Await.result(api.getAccessToken, Duration.Inf) must be(Left(Errors.twitterConnection("Cannot parse access token!")))
    }

    "get the access token properly" in {
      val api = new TwitterApi(cache, config.twitter, new MockHttpClient(validAccessTokenResponse))

      Await.result(api.getAccessToken, Duration.Inf) must be(Right("test"))
    }
  }

  private val cache: MockCache = {
    val c = new MockCache()
    c.set("accessToken", "test")
    c
  }

  private val validTweetsResponse: Json =
    Json.obj(
      "statuses" -> Json.arr(
        Json.obj("text" := "Tweet 1"),
        Json.obj("text" := "Tweet 2"),
        Json.obj("text" := "Tweet 3")
      )
    )

  private val invalidTweetsResponse1: Json = Json.obj("foo" := "bar")

  private val invalidTweetsResponse2: Json =
    Json.obj(
      "statuses" -> Json.arr(
        Json.obj("text" := "Tweet 1"),
        "foo".asJson
      )
    )

  private val validAccessTokenResponse: Json = Json.obj("access_token" := "test")

  private val invalidAccessTokenResponse: Json = Json.obj("foo" := "bar")
}
