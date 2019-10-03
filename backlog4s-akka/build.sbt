name := "backlog4s-akka"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.25",
  "co.fs2" %% "fs2-io" % "2.0.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
