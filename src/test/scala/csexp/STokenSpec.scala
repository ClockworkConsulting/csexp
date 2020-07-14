package csexp

import csexp.tokenize.SToken
import org.scalatest.flatspec.AnyFlatSpec

class STokenSpec extends AnyFlatSpec {

  behavior of "SToken.Atom#equals method"

  it should "do deep comparison on the byte array" in {
    // Setup
    val a = SToken.TAtom(Array(1, 2, 3))
    val b = SToken.TAtom(Array(1, 2, 3)) // Value equal, non-equal reference
    assert(!(a.bytes eq b.bytes)) // Just to make absoultely sure
    // Verify
    assert(a == b)
  }

  it should "compare equal-prefix arrays of different lengths as NOT equal" in {
    // Setup
    val a = SToken.TAtom(Array(1, 2, 3, 4))
    val b = SToken.TAtom(Array(1, 2, 3)) // Value equal, non-equal reference
    // Verify
    assert(a != b)
  }

}
