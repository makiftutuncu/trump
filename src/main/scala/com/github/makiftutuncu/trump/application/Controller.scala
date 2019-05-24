package com.github.makiftutuncu.scalacandidatetest.application

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.HttpEntity.{ChunkStreamPart, Chunked}
import akka.http.scaladsl.model.{ContentTypes, HttpResponse}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.directives.FutureDirectives
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.scaladsl.Source
import com.github.makiftutuncu.scalacandidatetest.domain.models.{Errors, MaybeF, ShoutError}
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax._

import scala.concurrent.ExecutionContext

abstract class Controller(implicit as: ActorSystem, ec: ExecutionContext) extends FutureDirectives with FailFastCirceSupport with StrictLogging {
  val route: Route

  val errorHandler =
    ExceptionHandler {
      case t: Throwable =>
        logger.error("Failed to handle request!", t)
        failWithError(Errors.unknown)
    }

  def respond[A: ToResponseMarshaller](maybe: MaybeF[A]): Route =
    onSuccess(maybe) {
      case Left(error)   => failWithError(error)
      case Right(result) => complete(result)
    }

  def failWithError(error: ShoutError): Route =
    complete {
      HttpResponse(
        error.code,
        entity = Chunked(
          ContentTypes.`application/json`,
          Source.single(error.asJson.noSpaces).map(ChunkStreamPart.apply)
        )
      )
    }
}
