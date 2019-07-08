package csexp

import java.io.EOFException
import java.io.InputStream

import scala.collection.mutable.ArrayBuffer

/**
 * Tokenizer for canonical s-expressions.
 */
object SExprTokenizer {

  /**
   * Positioned input-stream with push-back capability.
   */
  private[this] class PositionedInputStream(private val underlying: InputStream) {

    /**
     * Current position.
     */
    def position: Int = _position

    /**
     * Current position.
     */
    private[this] var _position: Int = 0

    /**
     * Read the next byte. Returns None if EOF has been reached.
     */
    def nextByte(): Option[Char] = {
      val ch = underlying.read()
      if (ch == -1) {
        None
      } else {
        _position += 1
        Some(ch.toChar)
      }
    }

    /**
     * Read a number of bytes into a buffer, throwing an EOFException
     * if the buffer cannot be filled before EOF is reached.
     */
    def nextBytes(n: Int): Array[Byte] = {
      val buf = new Array[Byte](n)
      var count = 0
      while (count < n) {
        // Read a bit more
        val readCount = underlying.read(buf, count, n - count)
        if (readCount < 0) {
          throw new EOFException()
        }
        // Move along
        count += readCount
        _position += readCount
      }
      buf
    }

  }

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

  /**
   * Tokens
   */
  sealed trait SToken

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
              tokens += (pos+1 -> TAtom(bytes))
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
