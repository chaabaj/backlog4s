package com.github.backlog4s.exceptions

import com.github.backlog4s.dsl.HttpError

case class BacklogApiException(error: HttpError) extends RuntimeException {
  override def getMessage: String =
    s"Request failed with error $error"
}
