import sbt._

object Dependencies {

  val scalaCheck =
    "org.scalacheck" %% "scalacheck" % "1.15.4"

  val scalaTest =
    "org.scalatest" %% "scalatest" % "3.2.10"

  val scalaTestPlusScalaCheck =
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0"

  val scodecBits =
    "org.scodec" %% "scodec-bits" % "1.1.29"

}
