//
// Project metadata
//

organization := "dk.cwconsult"

name := "csexp"

//
// Compiler settings
//

val scala_2_10 = "2.10.6"
val scala_2_11 = "2.11.11"
val scala_2_12 = "2.12.11"
val scala_2_13 = "2.13.3"

scalaVersion in ThisBuild := scala_2_13

crossScalaVersions := Seq(scala_2_10, scala_2_12, scala_2_12, scala_2_13)

scalacOptions in ThisBuild ++= Seq(
  "-Xlint",
   "-deprecation",
   "-unchecked",
   "-feature",
   "-encoding", "utf8"
)

//
// sbt-pgp settings
//

useGpg := true

//
// SonaType OSS publishing settings
//
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

//
// Test settings
//

testOptions in ThisBuild in Test +=  Tests.Argument(
  TestFrameworks.ScalaTest,
  "-oD", // Console output
  "-u", "%s" format ((target in Test).value / "test-reports")) // Output for Jenkins

//
// Dependencies
//

libraryDependencies ++= Seq(
  Dependencies.scalaCheck % "test",
  Dependencies.scalaTest % "test",
  Dependencies.scalaTestPlusScalaCheck % "test",
)
