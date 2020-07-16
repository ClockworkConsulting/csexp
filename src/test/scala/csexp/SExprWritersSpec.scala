package csexp

import java.nio.charset.Charset

import csexp.ast.SExpr._
import org.scalatest.flatspec.AnyFlatSpec
import scodec.bits.ByteVector
import csexp.impl.CompatSyntax._

class SExprWritersSpec extends AnyFlatSpec {

  // Shorthand for convenience
  private[this] val UTF_8 = Charset.forName("UTF-8")

  private[this] def fromUtf8(s: String): ByteVector =
    ByteVector.encodeUtf8(s).getOrThrow

  // ---------------------------------------------------------------------

  behavior of "SExprWriters.writeToByteArray"

  it should "be able serialize the Wikipedia example for canonical s-expressions" in {
    // Setup
    val sexpr =
      SList(
        SAtom(fromUtf8("this")),
        SAtom(fromUtf8("Canonical S-expression")),
        SAtom(fromUtf8("has")),
        SAtom(fromUtf8("5")),
        SAtom(fromUtf8("atoms")))

    // Exercise
    val serializedSExpr = SExprWriters.writeToByteArray(sexpr)

    // Verify
    assert(new String(serializedSExpr, UTF_8) === "(4:this22:Canonical S-expression3:has1:55:atoms)")
  }

}
