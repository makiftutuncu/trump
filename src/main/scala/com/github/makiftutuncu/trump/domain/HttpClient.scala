package com.github.makiftutuncu.scalacandidatetest.domain

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait HttpClient {
  def sendRequest(request: HttpRequest): Future[HttpResponse]
}
