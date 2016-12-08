//
// Project metadata
//

organization := "dk.cwconsult"

name := "csexp"

version in ThisBuild := "1.2.0-SNAPSHOT"

//
// Compiler settings
//

scalaVersion in ThisBuild := "2.12.1"

crossScalaVersions := Seq("2.11.8")

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
  Dependencies.scalaTest % "test",
  Dependencies.scalaCheck % "test"
)
