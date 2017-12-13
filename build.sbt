name := "backlog4s"

version := "1.0"

scalaVersion := "2.12.3"

resolvers += "krasserm at bintray" at "https://dl.bintray.com/krasserm/maven"

libraryDependencies ++= Seq(
  // Will be separated in different projects soon
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8",
  "com.github.krasserm" %% "streamz-converter" % "0.9-M1", // uses FS2 0.10.0
  //

  // Core dependencies
  "io.spray" %%  "spray-json" % "1.3.3",
  "org.typelevel" %% "cats-core" % "1.0.0-RC1",
  "org.typelevel" %% "cats-kernel" % "1.0.0-RC1",
  "org.typelevel" %% "cats-macros" % "1.0.0-RC1",
  "org.typelevel" %% "cats-free" % "1.0.0-RC1",
  "co.fs2" %% "fs2-core" % "0.10.0-M8",
  "co.fs2" %% "fs2-io" % "0.10.0-M8"
  //
)