package csexp

import java.nio.charset.Charset
import java.io.{OutputStream, ByteArrayInputStream, ByteArrayOutputStream, InputStream, EOFException}

import csexp.AST._
import scala.collection.mutable.ListBuffer

/**
 * Input for parsing was malformed. The position is the byte
 * position of the error.
 */
class MalformedInputException(
    position: Int,
    message: String)
  extends RuntimeException(s"$message: at position $position")

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
    override def equals(_other: Any) =
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
    val tokens = ListBuffer[(Int, SToken)]()

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

/**
 * Writers for canonical s-expressions.
 */
object SExprWriters {

  import SExprTokenizer._ // Token type defs

  // US-ASCII character set. This must be defined for ANY JVM implementation
  // (per standard) and so cannot throw.
  private[this] val US_ASCII = Charset.forName("US-ASCII")

  // Shorthand to avoid syntactic clutter.
  private[this] val LPAREN = "(".getBytes(US_ASCII)
  private[this] val RPAREN = ")".getBytes(US_ASCII)

  /**
   * Token writer.
   */
  class TokenWriter(outputStream: OutputStream) {

    /**
     * Write a token to the output stream.
     */
    def write(token: SToken): Unit = {
      token match {
        case TAtom(bytes) =>
          outputStream.write(s"${bytes.length}:".getBytes(US_ASCII))
          outputStream.write(bytes)
        case TLeftParenthesis =>
          outputStream.write(LPAREN)
        case TRightParenthesis =>
          outputStream.write(RPAREN)
      }
    }

  }

  /**
   * Write a canonical S-expression to a stream.
   */
  def writeToOutputStream(expr: SExpr, outputStream: OutputStream): Unit = {

    val tokenWriter = new TokenWriter(outputStream)

    def writeExpr(expr: SExpr): Unit = {
      expr match {
        case SAtom(bytes) =>
          tokenWriter.write(TAtom(bytes))
        case SList(elements @ _*) =>
          tokenWriter.write(TLeftParenthesis)
          for (element <- elements) {
            writeToOutputStream(element, outputStream)
          }
          tokenWriter.write(TRightParenthesis)
      }
    }

    writeExpr(expr)
  }

  /**
   * Write a canonical S-expression to a byte array. This function
   * is provided as a convenience; if you're writing to an output
   * stream, the function for writing directly to an output stream
   * should be used instead.
   */
  def writeToByteArray(sexpr: SExpr): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream(2048)
    writeToOutputStream(sexpr, outputStream)
    outputStream.close()
    outputStream.toByteArray
  }

}

/**
 * Parsers for canonical s-expressions.
 */
object SExprParsers {

  import SExprTokenizer._

  /**
   * Parse a canonical S-expression from a stream of tokens.
   */
  def parseFromStream(_stream: Vector[(Int, SToken)]): SExpr = {
    // Reference to our position in the stream.
    var stream = _stream

    // Throw an error with position information.
    def error(position: Int, message: String): Exception =
      throw new MalformedInputException(position, message)

    // Parse a list.
    def list(): Seq[SExpr] = {
      var elements = Vector[SExpr]()
      // Keep consuming elements until we see the RPAREN.
      while (stream.head._2 != TRightParenthesis) {
        elements = elements :+ atomOrList()
      }
      // Consume the terminating RPAREN
      stream = stream.tail
      // Return the elements
      elements
    }

    def atomOrList(): SExpr = {
      // Grab the next token
      val token = stream.headOption
      stream = stream.tail
      // Expecting either an atom or a list.
      token match {
        case Some((_, TAtom(bytes))) => SAtom(bytes)
        case Some((_, TLeftParenthesis)) => SList(list() :_ *)
        case Some((pos, token)) => throw error(pos, s"Unexpected token $token")
        case None => throw error(-1, "Unexpected EOF")
      }
    }

    // Initial non-terminal
    atomOrList()
  }

  /**
   * Parse a canonical S-expression from a stream.
   */
  def parseFromInputStream(inputStream: InputStream): SExpr =
    parseFromStream(tokenize(inputStream))

  /**
   * Parse a canonical s-expression from a byte array.
   */
  def parseFromByteArray(bytes: Array[Byte]): SExpr = {
    val inputStream = new ByteArrayInputStream(bytes)
    val sexpr = parseFromInputStream(inputStream)
    inputStream.close()
    sexpr
  }

}
