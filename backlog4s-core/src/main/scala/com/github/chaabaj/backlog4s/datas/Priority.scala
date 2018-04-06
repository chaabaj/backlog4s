package com.github.chaabaj.backlog4s.datas

case class Priority(
  id: Id[Priority],
  name: String
)

object PriorityT {
  def id(value: Int): Id[Priority] = Id(value)
}