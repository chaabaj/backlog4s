package com.github.backlog4s.datas

case class Id[A](value: Long) extends AnyVal
case class Key[A](value: String) extends AnyVal

sealed trait IdOrKeyParam[A] {
  override def toString: String
}
case class IdParam[A](id: Id[A]) extends IdOrKeyParam[A] {
  override def toString: String = id.value.toString
}
case class KeyParam[A](key: Key[A]) extends IdOrKeyParam[A] {
  override def toString: String = key.value
}
