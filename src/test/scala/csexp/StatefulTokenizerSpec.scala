package csexp

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import csexp.ast.SExpr
import csexp.ast.SExpr.SAtom
import csexp.ast.SExpr.SList
import csexp.tokenize.SToken
import csexp.tokenize.SToken.TAtom
import csexp.tokenize.SToken.TLeftParenthesis
import csexp.tokenize.SToken.TRightParenthesis
import csexp.tokenize.StatefulTokenizer
import org.scalatest.flatspec.AnyFlatSpec
import scodec.bits.ByteVector
import csexp.impl.CompatSyntax._

import scala.collection.mutable.ArrayBuffer

class StatefulTokenizerSpec extends AnyFlatSpec {

  private[this] def satom(s: String): SAtom =
    SAtom(ByteVector(s.getBytes(StandardCharsets.UTF_8)))

  private[this] def list(exprs: SExpr*): SList =
    SList(exprs :_ *)

  private[this] def tatom(s: String): TAtom =
    TAtom(ByteVector(s.getBytes(StandardCharsets.UTF_8)))

  private[this] def tokensToString(tokens: collection.Seq[SToken]): String = {
    // For readability if tests fail, we assume that atoms don't
    // contain delimiters.
    tokens.map {
      case TAtom(bytes) => bytes.decodeUtf8.getOrThrow
      case TLeftParenthesis => "("
      case TRightParenthesis => ")"
    }.mkString(" ")
  }

  // ------------------------------------------------------

  sealed trait Step

  object Step {

    /**
      * Next token.
      */
    case class N(expectedToken: SToken) extends Step

    /**
      * Skip1
      */
    case object S1 extends Step

  }

  // ------------------------------------------------------

  class Fixture {

    def test(
      sexpr: SExpr,
      expectations: Seq[Step])
    : Unit = {
      // Setup
      val statefulTokenizer: StatefulTokenizer = {
        val bytes = SExprWriters.writeToByteArray(sexpr)
        StatefulTokenizer(new ByteArrayInputStream(bytes))
      }
      // Exercise
      val recordedTokens = ArrayBuffer.empty[(Int, SToken)]
      val expectedTokens = ArrayBuffer.empty[SToken]
      expectations.foreach {
        case Step.N(expectedToken) =>
          expectedTokens.append(expectedToken)
          recordedTokens += statefulTokenizer.nextToken()
        case Step.S1 =>
          statefulTokenizer.skip1()
      }
      // Verify
      val actual = tokensToString(recordedTokens.map(_._2))
      val expected = tokensToString(expectedTokens.toVector)
      assert(actual === expected)
    }

  }

  // Shorthand
  import Step._

  // ------------------------------------------------------

  behavior of "StatefulTokenizer#skip1 method"

  it should "should skip a single atom" in new Fixture {
    test(
      sexpr =
        list(
          satom("a"),
          satom("b"),
          satom("c")
        ),
      expectations = Seq(
        N(TLeftParenthesis),
        N(tatom("a")),
        S1,
        N(tatom("c")),
        N(TRightParenthesis)
      )
    )
  }

  it should "should skip simple non-nested sub-expressions" in new Fixture {
    test(
      sexpr =
        list(
          satom("a"),
          satom("b"),
          list(
            satom("ca"),
            satom("cb")
          ),
          satom("d")
        ),
      expectations = Seq(
        N(TLeftParenthesis),
        N(tatom("a")),
        N(tatom("b")),
        S1,
        N(tatom("d")),
        N(TRightParenthesis)
      )
    )
  }

  it should "should skip arbitrarily large sub-expressions #0" in new Fixture {
    test(
      sexpr =
        list(
          satom("a"),
          satom("b"),
          list(
            satom("ca"),
            satom("cb"),
            list(
              satom("cba"),
              satom("cbb"),
              satom("cbc"),
              satom("cbd"),
              list(
                satom("cbda")
              ),
              satom("cbe")
            )
          ),
          satom("d")
        ),
      expectations = Seq(
        N(TLeftParenthesis),
        N(tatom("a")),
        N(tatom("b")),
        S1,
        N(tatom("d")),
        N(TRightParenthesis)
      )
    )
  }

  it should "should skip arbitrarily large sub-expressions #1" in new Fixture {
    test(
      sexpr =
        list(
          satom("a"),
          satom("b"),
          list(
            satom("ca"),
            satom("cb"),
            list(
              satom("cba"),
              satom("cbb"),
              satom("cbc"),
              satom("cbd"),
              list(
                satom("cbda")
              ),
              satom("cbe")
            )
          ),
          satom("d")
        ),
      expectations = Seq(
        N(TLeftParenthesis),
        N(tatom("a")),
        N(tatom("b")),
        N(TLeftParenthesis),
        N(tatom("ca")),
        S1,
        S1,
        N(TRightParenthesis),
        N(tatom("d")),
        N(TRightParenthesis)
      )
    )
  }

}
