package com.github.chaabaj.backlog4s.datas

import com.github.chaabaj.backlog4s.datas.ApiErrorCode.ApiErrorCode

/**
  * See backlog error api documentation
  * https://developer.nulab-inc.com/docs/backlog/error-response/
  */
object ApiErrorCode extends Enumeration {
  type ApiErrorCode = Value

  val InternalError = Value(1)
  val LicenceError = Value(2)
  val LicenceExpiredError = Value(3)
  val AccessDeniedError = Value(4)
  val UnauthorizedOperationError = Value(5)
  val NoResourceError = Value(6)
  val InvalidRequestError = Value(7)
  val SpaceOverCapacityError = Value(8)
  val ResourceOverflowError = Value(9)
  val TooLargeFileError = Value(10)
  val AuthenticationError = Value(11)
}

case class ApiError(
  message: String,
  code: ApiErrorCode,
  moreInfo: String
)

case class ApiErrors(errors: Seq[ApiError] = Seq())