package csexp

import java.io.ByteArrayInputStream
import java.io.InputStream

import csexp.ast.SExpr
import csexp.ast.SExpr._
import csexp.tokenize.SToken
import csexp.tokenize.SToken._

/**
 * Parsers for canonical s-expressions.
 */
object SExprParsers {

  import csexp.tokenize.SExprTokenizer._

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
