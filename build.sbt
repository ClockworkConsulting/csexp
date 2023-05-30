//
// Project metadata
//

organization := "dk.cwconsult"

name := "csexp"

//
// Auto-reload on build changes
//
Global / onChangedBuildSource := ReloadOnSourceChanges

//
// Compiler settings
//

val scala_2_12 = "2.12.17"
val scala_2_13 = "2.13.10"
val scala_3 = "3.3.0"

ThisBuild / scalaVersion := scala_2_13

ThisBuild / crossScalaVersions := Seq(scala_2_12, scala_2_13, scala_3)

ThisBuild / scalacOptions ++= Seq(
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

ThisBuild / Test / testOptions += Tests.Argument(
  TestFrameworks.ScalaTest,
  "-oD", // Console output
  "-u", "%s" format ((Test / target).value / "test-reports")) // Output for Jenkins

//
// Dependencies
//

libraryDependencies ++= Seq(
  Dependencies.scalaCheck % "test",
  Dependencies.scalaTest % "test",
  Dependencies.scalaTestPlusScalaCheck % "test",
  Dependencies.scodecBits,
)
