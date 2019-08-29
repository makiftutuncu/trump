package com.github.makiftutuncu.trump.domain.models

import io.circe.Encoder

case class Tweet(text: String) {
  lazy val shouted: String = {
    val iterator = text.trim.reverseIterator.dropWhile(c => c == '!' || c == '.')

    if (iterator.isEmpty) {
      ""
    } else {
      iterator
        .foldLeft(new StringBuilder("!")) {
          case (builder, c) => builder.insert(0, c.toUpper)
        }
        .toString
    }
  }
}

object Tweet {
  implicit val tweetEncoder: Encoder[Tweet] = Encoder.encodeString.contramap(_.shouted)
}
