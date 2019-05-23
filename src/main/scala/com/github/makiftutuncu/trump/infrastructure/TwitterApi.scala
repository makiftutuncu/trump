package com.github.makiftutuncu.trump.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.model.{FormData, HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.makiftutuncu.trump.Config.Twitter
import com.github.makiftutuncu.trump.domain.Maybe.EitherExtensions
import com.github.makiftutuncu.trump.domain._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.{Decoder, Json}

import scala.concurrent.ExecutionContext

class TwitterApi(val config: Twitter)(implicit as: ActorSystem, ec: ExecutionContext, m: Materializer) extends TweetRepository with FailFastCirceSupport {
  override def getTweets(username: String, limit: Int): MaybeF[List[Tweet]] = {
    getAccessToken.flatMap {
      case Left(error) =>
        MaybeF.error(error)

      case Right(accessToken) =>
        val parameters = Map("q" -> s"from:$username", "result_type" -> "recent", "count" -> limit.toString)

        val request =
          HttpRequest()
            .withMethod(HttpMethods.GET)
            .withUri(Uri("https://api.twitter.com/1.1/search/tweets.json").withQuery(Query(parameters)))
            .withHeaders(Authorization(OAuth2BearerToken(accessToken)))

        val result =
          for {
            httpResponse <- Http().singleRequest(request)
            json         <- Unmarshal(httpResponse).to[Json]
            tweets       <- MaybeF.maybe(parseTweets(json))
          } yield {
            tweets
          }

        result.recover {
          case t: Throwable =>
            Maybe.error(Errors.twitterConnection(s"Cannot get tweets! ${t.getMessage}"))
        }
    }
  }

  private def getAccessToken: MaybeF[String] = {
    val request =
      HttpRequest()
        .withMethod(HttpMethods.POST)
        .withUri(Uri("https://api.twitter.com/oauth2/token"))
        .withHeaders(Authorization(BasicHttpCredentials(config.apiKey, config.apiSecret)))
        .withEntity(FormData(Map("grant_type" -> "client_credentials")).toEntity)

    val result =
      for {
        httpResponse <- Http().singleRequest(request)
        json         <- Unmarshal(httpResponse).to[Json]
        maybeToken   <- MaybeF.maybe(parse[String](json, "access_token", "Cannot parse access token!"))
      } yield {
        maybeToken
      }

    result.recover {
      case t: Throwable =>
        Maybe.error(Errors.twitterConnection(s"Cannot get access token! ${t.getMessage}"))
    }
  }

  private def parseTweets(json: Json): Maybe[List[Tweet]] =
    parse[List[Json]](json, "statuses", "Cannot parse tweets!").flatMap { statuses =>
      statuses.foldLeft(Maybe.value(List.empty[Tweet])) {
        case (error @ Left(_), _)    => error
        case (Right(tweets), status) => parse[String](status, "text", "Cannot parse tweets!").map(t => tweets :+ Tweet(t))
      }
    }

  private def parse[A: Decoder](json: Json, key: String, error: String): Maybe[A] =
    json
      .hcursor
      .get[A](key)
      .failWith(df => Errors.twitterConnection(s"$error ${df.getMessage}"))
}
