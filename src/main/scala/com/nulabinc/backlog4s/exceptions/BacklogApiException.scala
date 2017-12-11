package com.nulabinc.backlog4s.exceptions

case class BacklogApiException(uri: String, status: Int, reason: String) extends RuntimeException {
  override def getMessage: String =
    s"Failed request for query $uri with status $status $reason"
}
