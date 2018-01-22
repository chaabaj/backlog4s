package backlog4s.apis

import backlog4s.datas.{AccessKey, Credentials, OAuth2Token}

trait Api {
  def credentials: Credentials
  def baseUrl: String
}

trait ApiContext[A <: Api] {
  def apply(baseUrl: String, credentials: Credentials): A
  def accessKey(baseUrl: String, accessKey: String): A =
    apply(baseUrl, AccessKey(accessKey))
  def oauthToken(baseUrl: String, token: String): A =
    apply(baseUrl, OAuth2Token(token))
}