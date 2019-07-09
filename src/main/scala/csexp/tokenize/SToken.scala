package csexp.tokenize

/**
 * Tokens
 */
sealed trait SToken

object SToken {

  /**
   * Left parenthesis.
   */
  case object TLeftParenthesis extends SToken

  /**
   * Right parenthesis.
   */
  case object TRightParenthesis extends SToken

  /**
   * Atom.
   */
  case class TAtom(bytes: Array[Byte]) extends SToken {
    override def equals(_other: Any): Boolean =
      _other.isInstanceOf[TAtom] && (_other.asInstanceOf[TAtom].bytes.deep == bytes.deep)
  }

}
