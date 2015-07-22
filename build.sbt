//
// Project metadata
//

organization := "dk.cwconsult"

name := "csexp"

version in ThisBuild := "1.1.2-SNAPSHOT"

//
// Compiler settings
//

crossScalaVersions := Seq("2.10.4")

scalaVersion in ThisBuild := "2.11.7"

scalacOptions in ThisBuild ++= Seq(
  "-Xlint",
   "-deprecation",
   "-unchecked",
   "-feature",
   "-encoding", "utf8"
)

//
// Test settings
//

testOptions in ThisBuild in Test <+= (target in Test) map {
  t => Tests.Argument(
    TestFrameworks.ScalaTest,
    "-oD", // Console output
    "-u", "%s" format (t / "test-reports")) // Output for Jenkins
}

//
// Dependencies
//

libraryDependencies ++= Seq(
  Dependencies.scalaTest % "test",
  Dependencies.scalaCheck % "test"
)
