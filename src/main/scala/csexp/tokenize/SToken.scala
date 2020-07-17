package csexp.tokenize

import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset

import csexp.impl.CompatSyntax._
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
  case class TAtom(bytes: ByteVector) extends SToken {

    /**
     * Decode a string from the token.
     */
    def decode(charset: Charset): Either[CharacterCodingException, String] =
      bytes.decodeString(charset)

    /**
     * Decode a string from the token.
     *
     * @throws CharacterCodingException
     *           if the string cannot be encoded into
     *           the given character set.
     */
    def decodeOrThrow(charset: Charset): String =
      decode(charset).getOrThrow

  }

  object TAtom {

    /**
     * Encode a string to a [[TAtom]].
     */
    def encode(s: String, charset: Charset): Either[CharacterCodingException, TAtom] =
      ByteVector.encodeString(s)(charset).mapRight(TAtom.apply)

    /**
     * Encode a string to a [[TAtom]].
     *
     * @throws CharacterCodingException
     *           if the string cannot be encoded into
     *           the given character set.
     */
    def encodeOrThrow(s: String, charset: Charset): TAtom =
      encode(s, charset).getOrThrow

  }

}
