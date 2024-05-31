ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "muun-blocklist"
  )


val http4sVersion = "1.0.0-M21"
//val CirceVersion = "0.14.0-M5"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  //"io.circe" %% "circe-generic" % CirceVersion,
  "ch.qos.logback" % "logback-classic" % "1.4.12",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
