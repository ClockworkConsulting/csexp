package csexp.impl

/**
 * Syntax enhancements which exist for compatbility across
 * Scala 2.11, 2.12 and 2.13.
 */
protected[csexp] object CompatSyntax {

  implicit class RichEither[A <: Throwable, B](val underlying: Either[A, B]) extends AnyVal {
    def getOrThrow: B = {
      underlying match {
        case Left(exc) => throw exc
        case Right(b) => b
      }
    }
  }

  implicit class RichEitherMap[A, B](val underlying: Either[A, B]) extends AnyVal {
    def mapRight[C](f: B => C): Either[A, C] = {
      underlying match {
        case Left(l) => Left(l)
        case Right(r) =>Right(f(r))
      }
    }
  }

}
