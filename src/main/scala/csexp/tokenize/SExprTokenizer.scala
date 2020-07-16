package csexp.tokenize

import java.io.InputStream

import csexp.MalformedInputException
import csexp.tokenize.SToken._
import scodec.bits.ByteVector

import scala.collection.mutable.ArrayBuffer

/**
 * Tokenizer for canonical s-expressions.
 */
object SExprTokenizer {

  // Digit matcher for syntactic convenience.
  private[this] object Digit {
    def unapply(ch: Char): Option[Char] = {
      if ((ch >= '0') && (ch <= '9')) {
        Some(ch)
      } else {
        None
      }
    }
  }

  // Constants to reduce syntactic clutter.
  private[this] val LPAREN = '('
  private[this] val RPAREN = ')'
  private[this] val COLON = ':'

  // State of the tokenizer.
  private[this] sealed trait TokenizerState
  private[this] case object TokenizerReady extends TokenizerState
  private[this] case class TokenizerAtom(lenPrefix: String) extends TokenizerState
  private[this] case object TokenizerDone extends TokenizerState

  /**
   * Tokenize an input stream consisting of canonical S-expressions.
   */
  def tokenize(_inputStream: InputStream): Vector[(Int, SToken)] = {
    // Current list of tokens. This is a bit silly and we should really
    // produce tokens as we go, but it'll do for now.
    val tokens = ArrayBuffer[(Int, SToken)]()

    // Input
    val input = new PositionedInputStream(_inputStream)

    // Generate an error message with a position indicator.
    def error(message: String): Exception =
      throw new MalformedInputException(input.position, message)

    // Parser
    var state : TokenizerState = TokenizerReady
    while (state != TokenizerDone) {
      // Save the position at the start of the iteration.
      val pos = input.position
      // Read the next byte from the stream.
      val ch = input.nextByte()
      // State machine.
      state match {
        // Ready state; ready for an atom, list or EOF.
        case TokenizerReady => {
          ch match {
            case None =>
              state = TokenizerDone
            case Some(LPAREN) =>
              tokens += (pos -> TLeftParenthesis)
              state = TokenizerReady
            case Some(RPAREN) =>
              tokens += (pos -> TRightParenthesis)
              state = TokenizerReady
            case Some(Digit(d)) =>
              state = TokenizerAtom(d.toString)
            case Some(ch) =>
              throw error(s"Unexpected character '$ch'")
          }
        }

        // Atom state; we're reading the length of an atom.
        case TokenizerAtom(lenPrefix) => {
          ch match {
            case None =>
              throw error("Unexpected EOF")
            case Some(Digit(d)) =>
              state = TokenizerAtom(lenPrefix + d.toString)
            case Some(COLON) =>
              // Read the atom contents
              val bytes = input.nextBytes(lenPrefix.toInt)
              tokens += (pos+1 -> TAtom(ByteVector.view(bytes)))
              state = TokenizerReady
            case Some(_) =>
              throw error(s"Unexpected character '$ch'")
          }
        }

        // Tokenizer done; shouldn't ever happen, but let's just ignore it.
        case TokenizerDone =>
          throw error(s"In TokenizerDone state even if not at EOF?")
      }
    }
    // Return the list of tokens.
    tokens.toVector
  }

}
