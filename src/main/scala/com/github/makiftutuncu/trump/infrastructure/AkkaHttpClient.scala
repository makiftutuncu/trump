package com.github.makiftutuncu.trump.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.github.makiftutuncu.trump.domain.HttpClient

import scala.concurrent.Future

class AkkaHttpClient(implicit as: ActorSystem) extends HttpClient {
  override def sendRequest(request: HttpRequest): Future[HttpResponse] = Http().singleRequest(request)
}
