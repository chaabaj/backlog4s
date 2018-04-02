package com.github.chaabaj.backlog4s.exceptions

import com.github.chaabaj.backlog4s.dsl.HttpError

case class BacklogApiException(error: HttpError) extends RuntimeException {
  override def getMessage: String =
    s"Request failed with error $error"
}
