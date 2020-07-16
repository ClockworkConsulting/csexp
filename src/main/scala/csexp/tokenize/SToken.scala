package csexp.tokenize

import scodec.bits.ByteVector

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
  case class TAtom(bytes: ByteVector) extends SToken

}
