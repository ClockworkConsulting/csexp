import sbt._

object Dependencies {

  val scalaCheck =
    "org.scalacheck" %% "scalacheck" % "1.14.3"

  val scalaTest =
    "org.scalatest" %% "scalatest" % "3.2.0"

  val scalaTestPlusScalaCheck =
    "org.scalatestplus" %% "scalacheck-1-14" % "3.1.0.0"

}
