
# Backlog SDK for Scala

## Goals

1. [ ] Support All Backlog Api (Testing)
2. [x] Modular can use any http library if user want to switch.
       For now by default we use akka-http.
       Later we will provide support for others libraries
3. [ ] Data aggregation
4. [ ] Streaming support
5. [ ] Setup webhook
6. [ ] OAuth2 support

## Optional Goals and interesting experiment

1. [ ] Scalajs support
2. [ ] Proxy server
3. [ ] GraphQL server
4. [ ] Usage of auto code generation for protocol by analyse json files at compile-time or using cli tools

## Installation

Not published yet on maven use this git repository

## How to use

First create a Key.scala file like this:
```scala
object ApiKey {
  val accessKey = "YOUR_API_KEY_HERE"
}
```

Look at backlog4s-test for sample code :)

