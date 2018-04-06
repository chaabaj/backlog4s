package com.github.chaabaj.backlog4s.datas

case class Resolution(
  id: Id[Resolution],
  name: String
)

object ResolutionT {
  def id(value: Int): Id[Resolution] = Id(value)
}