ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "demo-sbt",
    libraryDependencies += "com.twitter" % "twitter-api-java-sdk" % "1.2.2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.10",
    libraryDependencies += "com.typesafe" % "config" % "1.4.2",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
  )
