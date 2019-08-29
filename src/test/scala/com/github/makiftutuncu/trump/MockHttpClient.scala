package com.github.makiftutuncu.trump

import akka.http.scaladsl.model.HttpEntity.{ChunkStreamPart, Chunked}
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, HttpResponse}
import akka.stream.scaladsl.Source
import com.github.makiftutuncu.trump.domain.HttpClient
import io.circe.Json

import scala.concurrent.Future

class MockHttpClient(responses: Json*) extends HttpClient {
  private var i: Int = 0

  override def sendRequest(request: HttpRequest): Future[HttpResponse] =
    if (i < responses.size) {
      val response = HttpResponse(entity = Chunked(ContentTypes.`application/json`, Source.single(responses(i).noSpaces).map(ChunkStreamPart.apply)))
      i += 1
      Future.successful(response)
    } else {
      Future.failed(new Exception(s"Http client is called more than ${responses.size} times, incorrect mocks?"))
    }
}
