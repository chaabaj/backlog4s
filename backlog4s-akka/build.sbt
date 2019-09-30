name := "backlog4s-akka"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.github.zainab-ali" %% "fs2-reactive-streams" % "0.8.0", // uses FS2 1.0.0-M5
  "co.fs2" %% "fs2-io" % "2.0.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)