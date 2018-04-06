package com.github.chaabaj.backlog4s.datas

case class Status(
  id: Id[Status],
  name: String
)

object StatusT {
  def id(value: Int): Id[Status] = Id(value)
}