package com.github.makiftutuncu.scalacandidatetest.domain

import scala.concurrent.{ExecutionContext, Future}

package object models {
  type Maybe[A]  = Either[ShoutError, A]
  type MaybeF[A] = Future[Maybe[A]]

  object Maybe {
    def error[A](e: ShoutError): Maybe[A] = Left(e)
    def value[A](a: A): Maybe[A]          = Right(a)

    implicit class EitherExtensions[L, R](either: Either[L, R]) {
      def failWith(error: ShoutError): Maybe[R] =
        either match {
          case Left(_)  => Maybe.error(error)
          case Right(r) => Maybe.value(r)
        }
    }
  }

  object MaybeF {
    def error[A](e: ShoutError): MaybeF[A]   = Future.successful(Maybe.error(e))
    def value[A](a: A): MaybeF[A]            = Future.successful(Maybe.value(a))
    def maybe[A](maybe: Maybe[A]): MaybeF[A] = Future.successful(maybe)

    def from[A](a: => A)(implicit ec: ExecutionContext): MaybeF[A] = fromFuture(Future(a))

    def fromFuture[A](future: Future[A])(implicit ec: ExecutionContext): MaybeF[A] = future.map(Maybe.value)
  }
}
