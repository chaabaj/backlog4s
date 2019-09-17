name := "backlog4s"

lazy val commonScalacOptions = Seq(
  "-deprecation"
)

lazy val commonSettings = Seq(
  version := "0.7.1",
  organization := "com.github.chaabaj",
  scalaVersion := "2.12.9",
  scalacOptions := commonScalacOptions
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

isSnapshot := version.value endsWith "SNAPSHOT"

lazy val publishPackages = Seq(
  useGpg := true,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := {
    val realm = "Sonatype Nexus Repository Manager"
    val staging = "https://oss.sonatype.org/content/repositories/snapshots"
    val release = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    if (isSnapshot.value)
      Some(realm at staging)
    else
      Some(realm at release)
  },
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  homepage := Some(url("https://github.com/chaabaj")),
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/chaabaj/backlog4s"), "scm:git:git@github.com/chaabaj/backlog4s.git"))
)

lazy val backlog4sCore = (project in file("backlog4s-core"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-core"
  )
  .settings(publishPackages)

lazy val backlog4sAkka = (project in file("backlog4s-akka"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-akka"
  )
  .dependsOn(backlog4sCore)
  .settings(publishPackages)

lazy val backlog4sHammock = (project in file("backlog4s-hammock"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-hammock"
  )
  .dependsOn(backlog4sCore)
  .settings(publishPackages)

lazy val backlog4sTest = (project in file("backlog4s-test"))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .dependsOn(backlog4sCore, backlog4sAkka, backlog4sHammock)

lazy val backlog4sGraphQl = (project in file("backlog4s-graphql"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-graphql"
  )
  .settings(publishPackages)
  .dependsOn(backlog4sCore, backlog4sAkka, backlog4sTest)

lazy val backlog4s = (project in file("."))
  .settings(moduleName := "root")
  .settings(noPublishSettings)
  .aggregate(
    backlog4sCore,
    backlog4sAkka,
    backlog4sHammock,
    backlog4sGraphQl
  )
