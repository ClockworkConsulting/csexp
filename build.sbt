//
// Project metadata
//

organization := "dk.cwconsult"

name := "csexp"

//
// Compiler settings
//

scalaVersion in ThisBuild := "2.12.3"

crossScalaVersions := Seq("2.11.11", "2.10.6")

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
