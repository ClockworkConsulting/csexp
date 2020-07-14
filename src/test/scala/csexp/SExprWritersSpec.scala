package csexp

import java.nio.charset.Charset

import csexp.ast.SExpr._
import org.scalatest.flatspec.AnyFlatSpec

class SExprWritersSpec extends AnyFlatSpec {

  // Shorthand for convenience
  private[this] val UTF_8 = Charset.forName("UTF-8")

  // ---------------------------------------------------------------------

  behavior of "SExprWriters.writeToByteArray"

  it should "be able serialize the Wikipedia example for canonical s-expressions" in {
    // Setup
    val sexpr =
      SList(
        SAtom("this".getBytes(UTF_8)),
        SAtom("Canonical S-expression".getBytes(UTF_8)),
        SAtom("has".getBytes(UTF_8)),
        SAtom("5".getBytes(UTF_8)),
        SAtom("atoms".getBytes(UTF_8)))

    // Exercise
    val serializedSExpr = SExprWriters.writeToByteArray(sexpr)

    // Verify
    assert(new String(serializedSExpr, UTF_8) === "(4:this22:Canonical S-expression3:has1:55:atoms)")
  }

}
