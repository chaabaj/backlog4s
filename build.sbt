name := "backlog4s"

lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.1"
lazy val supportedScalaVersions = List(scala213, scala212)

ThisBuild / organization := "com.github.chaabaj"
ThisBuild / version      := "0.8.1"
ThisBuild / scalaVersion := scala212

lazy val commonScalacOptions = Seq(
  "-deprecation"
)

lazy val commonSettings = Seq(
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
    name := "backlog4s-core",
    crossScalaVersions := supportedScalaVersions,
    publishPackages
  )

lazy val backlog4sAkka = (project in file("backlog4s-akka"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-akka"
  )
  .dependsOn(backlog4sCore)
  .settings(
    publishPackages,
    crossScalaVersions := supportedScalaVersions
  )

lazy val backlog4sHammock = (project in file("backlog4s-hammock"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-hammock",
    // hammock-core doesn't support Scala 2.13
    crossScalaVersions := List(scala212),
    publishPackages
  )
  .dependsOn(backlog4sCore)

lazy val backlog4sTest = (project in file("backlog4s-test"))
  .settings(
    commonSettings,
    noPublishSettings,
    crossScalaVersions := List(scala212)
  )
  .dependsOn(backlog4sCore, backlog4sAkka, backlog4sHammock)

lazy val backlog4sGraphQl = (project in file("backlog4s-graphql"))
  .settings(commonSettings)
  .settings(
    name := "backlog4s-graphql",
    crossScalaVersions := supportedScalaVersions,
    publishPackages
  )
  .dependsOn(backlog4sCore, backlog4sAkka)

lazy val backlog4s = (project in file("."))
  .settings(
    moduleName := "root",
    crossScalaVersions := Nil,
    noPublishSettings
  )
  .aggregate(
    backlog4sCore,
    backlog4sAkka,
    backlog4sHammock,
    backlog4sGraphQl
  )
