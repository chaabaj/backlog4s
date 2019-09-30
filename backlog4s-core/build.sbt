name := "backlog4s-core"

val catsVersion = "2.0.0"
val monixVersion = "3.0.0"

libraryDependencies ++= Seq(
  "io.spray"      %% "spray-json"     % "1.3.5",
  "org.typelevel" %% "cats-core"      % catsVersion,
  "org.typelevel" %% "cats-kernel"    % catsVersion,
  "org.typelevel" %% "cats-macros"    % catsVersion,
  "org.typelevel" %% "cats-free"      % catsVersion,
  "io.monix"      %% "monix"          % monixVersion,
  "io.monix"      %% "monix-eval"     % monixVersion,
  "io.monix"      %% "monix-reactive" % monixVersion,
  "joda-time"     %  "joda-time"      % "2.9.9",
  "co.fs2"        %% "fs2-core"       % "2.0.1",
  // Test //
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)