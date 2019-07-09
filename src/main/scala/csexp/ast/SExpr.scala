package csexp.ast

import java.util

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
  case class SAtom(bytes: Array[Byte]) extends SExpr {

    override def equals(other: Any): Boolean = {
      other.isInstanceOf[SAtom] && util.Arrays.equals(bytes, other.asInstanceOf[SAtom].bytes)
    }

  }

}
