name := "backlog4s-akka"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "co.fs2" %% "fs2-io" % "0.10.3",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
