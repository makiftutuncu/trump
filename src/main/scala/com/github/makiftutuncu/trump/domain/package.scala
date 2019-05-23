package com.github.makiftutuncu.trump

import scala.concurrent.Future

package object domain {
  type Maybe[A]  = Either[ShoutError, A]
  type MaybeF[A] = Future[Maybe[A]]

  object Maybe {
    def error[A](e: ShoutError): Maybe[A] = Left(e)
    def value[A](a: A): Maybe[A]          = Right(a)

    implicit class EitherExtensions[L, R](either: Either[L, R]) {
      def failWith(toError: L => ShoutError): Maybe[R] =
        either match {
          case Left(l)  => Maybe.error(toError(l))
          case Right(r) => Maybe.value(r)
        }
    }
  }

  object MaybeF {
    def error[A](e: ShoutError): MaybeF[A]   = Future.successful(Maybe.error(e))
    def value[A](a: A): MaybeF[A]            = Future.successful(Maybe.value(a))
    def maybe[A](maybe: Maybe[A]): MaybeF[A] = Future.successful(maybe)
  }
}
