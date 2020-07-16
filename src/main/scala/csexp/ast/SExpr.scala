package csexp.ast

import scodec.bits.ByteVector

/**
 * AST node for a canonical S-expression.
 */
sealed trait SExpr

object SExpr {

  /**
   * AST node representing a list of s-expressions.
   */
  case class SList(elements: SExpr*) extends SExpr

  /**
   * AST node representing an atom.
   */
  case class SAtom(bytes: ByteVector) extends SExpr

}
