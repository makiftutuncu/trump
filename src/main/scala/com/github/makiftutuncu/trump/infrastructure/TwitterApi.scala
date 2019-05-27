package com.github.makiftutuncu.scalacandidatetest.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.model.{FormData, HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.makiftutuncu.scalacandidatetest.Config.Twitter
import com.github.makiftutuncu.scalacandidatetest.domain._
import com.github.makiftutuncu.scalacandidatetest.domain.models.Maybe.EitherExtensions
import com.github.makiftutuncu.scalacandidatetest.domain.models.{Errors, Maybe, MaybeF, Tweet}
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.{Decoder, Json}

import scala.concurrent.ExecutionContext

class TwitterApi(val cache: Cache[String],
                 val config: Twitter,
                 val httpClient: HttpClient)(implicit as: ActorSystem,
                                                      ec: ExecutionContext,
                                                       m: Materializer) extends TweetRepository
                                                                           with FailFastCirceSupport
                                                                           with StrictLogging {
  override def getTweets(username: String, limit: Int): MaybeF[List[Tweet]] = {
    logger.debug(s"Going to try and get $limit tweets of user $username")

    cache.use("accessToken", config.accessTokenTTL)(getAccessToken)(identity)(at => MaybeF.value(at)).flatMap {
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
            httpResponse <- httpClient.sendRequest(request)
            json         <- Unmarshal(httpResponse).to[Json]
            _             = logger.debug("Got tweets response")
            _             = logger.trace(json.noSpaces)
            tweets       <- MaybeF.maybe(parseTweets(json))
          } yield {
            tweets
          }

        result.recover {
          case t: Throwable =>
            logger.error(s"Failed to get tweets of user $username", t)
            Maybe.error(Errors.twitterConnection("Cannot get tweets!"))
        }
    }
  }

  protected[infrastructure] def getAccessToken: MaybeF[String] = {
    logger.debug("Going to get an access token")

    val request =
      HttpRequest()
        .withMethod(HttpMethods.POST)
        .withUri(Uri("https://api.twitter.com/oauth2/token"))
        .withHeaders(Authorization(BasicHttpCredentials(config.apiKey, config.apiSecret)))
        .withEntity(FormData(Map("grant_type" -> "client_credentials")).toEntity)

    val result =
      for {
        httpResponse <- httpClient.sendRequest(request)
        json         <- Unmarshal(httpResponse).to[Json]
        _             = logger.debug("Got access token response")
        _             = logger.trace(json.noSpaces)
        maybeToken   <- MaybeF.maybe(parse[String](json, "access_token", "Cannot parse access token!"))
      } yield {
        maybeToken
      }

    result.recover {
      case t: Throwable =>
        logger.error("Failed to get access token", t)
        Maybe.error(Errors.twitterConnection("Cannot get access token!"))
    }
  }

  private def parseTweets(json: Json): Maybe[List[Tweet]] =
    parse[List[Json]](json, "statuses", "Cannot parse tweets!").flatMap { statuses =>
      statuses.foldLeft(Maybe.value(List.empty[Tweet])) {
        case (error @ Left(_), _) =>
          error

        case (Right(tweets), status) =>
          parse[String](status, "text", "Cannot parse tweets!").map { text =>
            logger.debug(s"Parsed tweet: $text")
            tweets :+ Tweet(text)
          }
      }
    }

  private def parse[A: Decoder](json: Json, key: String, error: String): Maybe[A] =
    json
      .hcursor
      .get[A](key)
      .failWith(Errors.twitterConnection(error))
}
