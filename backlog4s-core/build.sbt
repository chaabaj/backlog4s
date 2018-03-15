name := "backlog4s-core"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.3",
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.typelevel" %% "cats-kernel" % "1.1.0",
  "org.typelevel" %% "cats-macros" % "1.1.0",
  "org.typelevel" %% "cats-free" % "1.1.0",
  "co.fs2" %% "fs2-core" % "0.10.0-RC1",
  "co.fs2" %% "fs2-io" % "0.10.0-RC1",
  "joda-time" % "joda-time" % "2.9.9",

  // Test //
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)