package csexp

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

import csexp.AST.SExpr._
import csexp.AST.SExpr

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