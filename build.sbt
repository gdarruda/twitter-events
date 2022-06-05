ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.1.2"

lazy val root = (project in file("."))
  .settings(
    name := "demo-sbt",
    libraryDependencies += "com.twitter" % "twitter-api-java-sdk" % "1.2.2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.10",
    libraryDependencies += "com.typesafe" % "config" % "1.4.2",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.28.0",
    libraryDependencies += "io.getquill" %% "quill-jdbc" % "3.17.0-Beta30-RC5",
    libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.2.0"
  )
