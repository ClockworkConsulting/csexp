package csexp

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets.UTF_8

import csexp.ast.SExpr._
import csexp.tokenize.SExprTokenizer
import csexp.tokenize.SToken._
import org.scalatest.flatspec.AnyFlatSpec
import scodec.bits.ByteVector
import csexp.impl.CompatSyntax._

class SExprParsersSpec extends AnyFlatSpec {

  // ---------------------------------------------------------------------

  val wikipediaExampleBytes =
    "(4:this22:Canonical S-expression3:has1:55:atoms)".getBytes(UTF_8)

  // ---------------------------------------------------------------------

  private[this] def fromUtf8(s: String): ByteVector =
    ByteVector.encodeUtf8(s).getOrThrow

  // ---------------------------------------------------------------------

  behavior of "SExprParsers.parseFromByteArray"

  it should "be able parse the Wikipedia example for canonical s-expressions" in {
    // Exercise
    val parsedSexpr = SExprParsers.parseFromByteArray(wikipediaExampleBytes)

    // Verify
    assert(parsedSexpr === SList(
      SAtom(fromUtf8("this")),
      SAtom(fromUtf8("Canonical S-expression")),
      SAtom(fromUtf8("has")),
      SAtom(fromUtf8("5")),
      SAtom(fromUtf8("atoms"))))
  }

  it should "parse a zero-sized csexpr" in {
    // Exercise
    val parsedSexpr = SExprParsers.parseFromByteArray(
      "0:".getBytes(UTF_8))

    // Verify
    assert(parsedSexpr === SAtom(ByteVector.empty))
  }

  // ---------------------------------------------------------------------

  behavior of "SExprParsers.tokenize"

  it should "return a correct stream for the Wikipedia example" in {
    // Setup
    val expectedTokens = Seq(
      0 -> TLeftParenthesis,
      3 -> TAtom.encodeOrThrow("this", UTF_8),
      10 -> TAtom.encodeOrThrow("Canonical S-expression", UTF_8),
      34 -> TAtom.encodeOrThrow("has", UTF_8),
      39 -> TAtom.encodeOrThrow("5", UTF_8),
      42 -> TAtom.encodeOrThrow("atoms", UTF_8),
      47 -> TRightParenthesis)

    // Exercise
    val tokenStream = SExprTokenizer.tokenize(new ByteArrayInputStream(wikipediaExampleBytes))

    // Verify
    assert(tokenStream === expectedTokens)
  }

  it should "return a correct stream for a zero-sized csexpr" in {
    // Setup
    val expectedTokens = Seq(
      2 -> TAtom.encodeOrThrow("", UTF_8),
    )

    // Exercise
    val tokenStream = SExprTokenizer.tokenize(new ByteArrayInputStream("0:".getBytes(UTF_8)))

    // Verify
    assert(tokenStream === expectedTokens)
  }

}
