package csexp

import java.io.ByteArrayInputStream
import java.nio.charset.Charset

import csexp.ast.SExpr._
import csexp.tokenize.SExprTokenizer
import csexp.tokenize.SToken._
import org.scalatest.flatspec.AnyFlatSpec

class SExprParsersSpec extends AnyFlatSpec {

  // Shorthand for convenience
  private[this] val UTF_8 = Charset.forName("UTF-8")

  // ---------------------------------------------------------------------

  val wikipediaExampleBytes =
    "(4:this22:Canonical S-expression3:has1:55:atoms)".getBytes(UTF_8)

  // ---------------------------------------------------------------------

  behavior of "SExprParsers.parseFromByteArray"

  it should "be able parse the Wikipedia example for canonical s-expressions" in {
    // Exercise
    val parsedSexpr = SExprParsers.parseFromByteArray(wikipediaExampleBytes)

    // Verify
    assert(parsedSexpr === SList(
      SAtom("this".getBytes(UTF_8)),
      SAtom("Canonical S-expression".getBytes(UTF_8)),
      SAtom("has".getBytes(UTF_8)),
      SAtom("5".getBytes(UTF_8)),
      SAtom("atoms".getBytes(UTF_8))))
  }

  // ---------------------------------------------------------------------

  behavior of "SExprParsers.tokenize"

  it should "return a correct stream for the Wikipedia example" in {
    // Setup
    val expectedTokens = Seq(
      0 -> TLeftParenthesis,
      3 -> TAtom("this".getBytes(UTF_8)),
      10 -> TAtom("Canonical S-expression".getBytes(UTF_8)),
      34 -> TAtom("has".getBytes(UTF_8)),
      39 -> TAtom("5".getBytes(UTF_8)),
      42 -> TAtom("atoms".getBytes(UTF_8)),
      47 -> TRightParenthesis)

    // Exercise
    val tokenStream = SExprTokenizer.tokenize(new ByteArrayInputStream(wikipediaExampleBytes))

    // Verify
    assert(tokenStream === expectedTokens.toStream)
  }

}
