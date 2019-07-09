package csexp

import org.scalatest.FlatSpec
import org.scalatest.prop.Checkers
import org.scalacheck._, Prop._, Arbitrary._, Gen._

import csexp.AST._
import csexp.AST.SExpr._

/**
 * Specifications for the canonical s-expression writing and parsing code.
 */
class SExprParsersWritersSpec extends FlatSpec with Checkers {

  // ---------------------------------------------------------------------
  // Generators for random instances of S-expressions.

  lazy val genAtom: Gen[SAtom] = for {
    bytes <- arbitrary[Array[Byte]]
  } yield SAtom(bytes)

  lazy val genList: Gen[SList] = for {
    n <- Gen.choose(0,2)
    elements <- listOfN(n, genSExpr)
  } yield SList(elements.toSeq :_ *)

  def genSExpr: Gen[SExpr] = oneOf(genAtom, genList)

  implicit val arbitrarySExpr = Arbitrary(genSExpr)

  // ---------------------------------------------------------------------

  behavior of "Canonical S-expression parsers/writers"

  it should "be able to round-trip arbitrary S-expressions" in {
    // Exercise/Verify
    check(Prop.forAll { sexpr: SExpr => {
      sexpr == SExprParsers.parseFromByteArray(SExprWriters.writeToByteArray(sexpr))
    }})
  }

}
