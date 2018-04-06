package com.github.chaabaj.backlog4s.datas

object CategoryT {
  def id(value: Int): Id[Category] = Id(value)
}

case class Category(
  id: Id[Category],
  name: String,
  displayOrder: Long
)

case class AddCategoryForm(
  name: String
)