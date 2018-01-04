name := "backlog4s"

scalaVersion := "2.12.3"

lazy val commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.12.3"
)

lazy val core = (project in file("backlog4s-core"))
  .settings(commonSettings)

lazy val akka = (project in file("backlog4s-akka"))
  .settings(commonSettings)
  .dependsOn(core)

lazy val hammock = (project in file("backlog4s-hammock"))
  .settings(commonSettings)
  .dependsOn(core)

lazy val backlog4sTest = (project in file("backlog4s-test"))
  .settings(commonSettings)
  .dependsOn(core, akka, hammock)
