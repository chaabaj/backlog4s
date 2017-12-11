name := "backlog4s"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8",
  "io.spray" %%  "spray-json" % "1.3.3",
  "org.typelevel" %% "cats-core" % "1.0.0-RC1",
  "org.typelevel" %% "cats-kernel" % "1.0.0-RC1",
  "org.typelevel" %% "cats-macros" % "1.0.0-RC1",
  "org.typelevel" %% "cats-free" % "1.0.0-RC1"
)