name := "backlog4s-akka"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8",
  "com.github.zainab-ali" %% "fs2-reactive-streams" % "0.2.6", // uses FS2 0.10.0
  "co.fs2" %% "fs2-core" % "0.10.0-M9",
  "co.fs2" %% "fs2-io" % "0.10.0-M9",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)