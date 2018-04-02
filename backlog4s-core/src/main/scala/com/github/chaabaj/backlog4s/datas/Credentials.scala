package com.github.chaabaj.backlog4s.datas

sealed trait Credentials
case class AccessKey(key: String) extends Credentials
case class OAuth2Token(token: String) extends Credentials
