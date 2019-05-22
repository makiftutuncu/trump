package com.github.makiftutuncu.trump.domain

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
}
