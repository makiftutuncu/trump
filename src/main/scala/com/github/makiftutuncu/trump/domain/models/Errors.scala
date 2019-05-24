package com.github.makiftutuncu.scalacandidatetest.domain.models

import akka.http.scaladsl.model.StatusCodes

object Errors {
  def invalidLimit(n: Int): ShoutError =
    ShoutError(
      StatusCodes.BadRequest.intValue,
      "invalid-limit",
      s"Limit $n is invalid. It must be between 1 and 10."
    )

  val unknown: ShoutError =
    ShoutError(
      StatusCodes.InternalServerError.intValue,
      "unknown",
      "An unknown error has occurred!"
    )

  def twitterConnection(details: String): ShoutError =
    ShoutError(
      StatusCodes.ServiceUnavailable.intValue,
      "twitter-connection",
      details
    )
}
