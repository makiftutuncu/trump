package com.github.makiftutuncu.trump.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.model.{FormData, HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.makiftutuncu.trump.domain.{Errors, ShoutError, Tweet, TweetRepository}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Json

import scala.concurrent.{ExecutionContext, Future}

class TwitterApi(implicit as: ActorSystem, ec: ExecutionContext, m: Materializer) extends TweetRepository with FailFastCirceSupport {
  val consumerAPIKey       = ""
  val consumerAPISecretKey = ""

  override def getTweets(username: String, limit: Int): Future[Either[ShoutError, List[Tweet]]] = {
    getAccessToken.flatMap {
      case Left(error) =>
        Future.successful(Left(error))

      case Right(accessToken) =>
        val uri = Uri("https://api.twitter.com/1.1/search/tweets.json").withQuery(Query(Map("q" -> s"from:$username", "result_type" -> "recent", "count" -> limit.toString)))

        val request =
          HttpRequest()
            .withMethod(HttpMethods.GET)
            .withUri(uri)
            .withHeaders(Authorization(OAuth2BearerToken(accessToken)))

        val result =
          for {
            httpResponse <- Http().singleRequest(request)
            json         <- Unmarshal(httpResponse).to[Json]
            tweets       <- Future.successful(parseTweets(json))
          } yield {
            tweets
          }

        result.recover {
          case t: Throwable =>
            Left(Errors.twitterConnection(s"Cannot get tweets! ${t.getMessage}"))
        }
    }
  }

  private def getAccessToken: Future[Either[ShoutError, String]] = {
    val request =
      HttpRequest()
        .withMethod(HttpMethods.POST)
        .withUri(Uri("https://api.twitter.com/oauth2/token"))
        .withHeaders(Authorization(BasicHttpCredentials(consumerAPIKey, consumerAPISecretKey)))
        .withEntity(FormData(Map("grant_type" -> "client_credentials")).toEntity)

    val result =
      for {
        httpResponse <- Http().singleRequest(request)
        json         <- Unmarshal(httpResponse).to[Json]
        maybeToken   <- Future.successful(json.hcursor.downField("access_token").as[String])
      } yield {
        maybeToken.fold(df => Left(Errors.twitterConnection(s"Cannot parse access token! ${df.getMessage()}")), at => Right(at))
      }

    result.recover {
      case t: Throwable =>
        Left(Errors.twitterConnection(s"Cannot get access token! ${t.getMessage}"))
    }
  }

  private def parseTweets(json: Json): Either[ShoutError, List[Tweet]] =
    json.hcursor.downField("statuses").as[List[Json]] match {
      case Left(decodingFailure) =>
        Left(Errors.twitterConnection(s"Cannot parse tweets! ${decodingFailure.getMessage}"))

      case Right(statuses) =>
        statuses.foldLeft[Either[ShoutError, List[Tweet]]](Right(List.empty)) {
          case (error @ Left(_), _) =>
            error

          case (Right(tweets), status) =>
            status.hcursor.downField("text").as[String] match {
              case Left(decodingFailure) => Left(Errors.twitterConnection(s"Cannot parse tweets! ${decodingFailure.getMessage}"))
              case Right(text)           => Right(tweets :+ Tweet(text))
            }
        }
    }
}
