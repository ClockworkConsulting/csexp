package csexp.tokenize

import java.io.InputStream
import java.nio.charset.StandardCharsets
import csexp.tokenize.SToken._

sealed abstract class StatefulTokenizer private(var stream: Vector[(Int, SToken)]) {

  def error(position: Int, message: String): Nothing = {
    if (position >= 0) {
      throw new IllegalArgumentException(s"$message at position $position.")
    } else {
      throw new IllegalArgumentException(message)
    }
  }

  def nextToken(): (Int, SToken) = {
    val h = stream.headOption match {
      case Some(token) => token
      case None => throw error(-1, "Unexpected EOF")
    }
    // Consume
    stream = stream.tail
    // Return the previous head
    h
  }

  def consumeLParen(): Unit = {
    nextToken() match {
      case (_  , TLeftParenthesis) => ()
      case (pos, token)            => throw error(pos, s"Expected '(', got $token")
    }
  }

  def consumeRParen(): Unit = {
    nextToken() match {
      case (_  , TRightParenthesis) => ()
      case (pos, token)             => throw error(pos, s"Expected ')', got $token")
    }
  }

  def consumeTag(): (Int, String) = {
    nextToken() match {
      case (pos, TAtom(tag)) => (pos, new String(tag, StandardCharsets.UTF_8))
      case (pos, token)      => throw error(pos, s"Expected tag, got $token")
    }
  }

}

object StatefulTokenizer {

  /**
    * Create a [[StatefulTokenizer]] from an immediable token stream.
    */
  def apply(tokens: Vector[(Int, SToken)]): StatefulTokenizer =
    new StatefulTokenizer(tokens) { }

  /**
    * Create a [[StatefulTokenizer]] from an input stream.
    */
  def apply(inputStream: InputStream): StatefulTokenizer =
    apply(SExprTokenizer.tokenize(inputStream))

}
