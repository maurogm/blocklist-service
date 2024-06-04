ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "muun-blocklist",
    scalacOptions ++= List("-Ymacro-annotations"), //For @newtype macro
  )


val http4sVersion = "1.0.0-M21"
//val CirceVersion = "0.14.0-M5"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  //"io.circe" %% "circe-generic" % CirceVersion,
  //"org.typelevel" %% "cats-effect" % "3.3.0",
  "org.typelevel" %% "log4cats-core" % "2.1.1",
  "org.typelevel" %% "log4cats-slf4j" % "2.1.1",
  "ch.qos.logback" % "logback-classic" % "1.4.12",
  "eu.timepit" %% "refined" % "0.10.1",
  "io.estatico" %% "newtype" % "0.4.4",
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
