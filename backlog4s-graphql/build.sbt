name := "backlog4s-graphql"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC1",
  "org.sangria-graphql" %% "sangria" % "1.3.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0"
)
