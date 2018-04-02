package backlog4s.dsl

import backlog4s.datas.{Id, IdOrKeyParam}
import backlog4s.formatters.SprayJsonFormats.DateTimeFormat
import org.joda.time.DateTime

sealed trait QueryParam {
  def name: String
  def encode: Map[String, String]
  def isEmpty: Boolean
  def nonEmpty: Boolean = !isEmpty
}

case class StringParam(name: String, value: String) extends QueryParam {
  override def encode: Map[String, String] =
    Map(name -> value)

  override def isEmpty: Boolean = value.isEmpty
}

case class ListParam[A](name: String, values: Seq[String]) extends QueryParam {
  override def isEmpty: Boolean = values.isEmpty
  override def encode: Map[String, String] = {
    values.zipWithIndex.foldLeft(Map.empty[String, String]) {
      case (acc, (value, index)) =>
        acc + (s"$name[$index]" -> value)
    }
  }

}

object EmptyParam extends QueryParam {
  override def name: String = ""
  override def encode: Map[String, String] = Map()
  override def isEmpty: Boolean = true
}

trait ToQueryParam[A] {
  def encode(name: String, a: A): QueryParam
}

object ToQueryParam {
  implicit def idParam[A] = new ToQueryParam[Id[A]] {
    override def encode(name: String, id: Id[A]): QueryParam =
      StringParam(name, id.value.toString)
  }

  implicit def idList[A] = new ToQueryParam[Seq[Id[A]]] {
    override def encode(name: String, ids: Seq[Id[A]]): QueryParam =
      ListParam(name, ids.map(_.value.toString))
  }

  implicit val stringParam = new ToQueryParam[String] {
    override def encode(name: String, str: String): QueryParam =
      StringParam(name, str)
  }
  implicit val dateTimeParam = new ToQueryParam[DateTime] {
    override def encode(name: String, dateTime: DateTime): QueryParam =
      StringParam(name, dateTime.toString(DateTimeFormat.formatter))
  }
  implicit val intParam = new ToQueryParam[Int] {
    override def encode(name: String, a: Int): QueryParam =
      StringParam(name, a.toString)
  }
  implicit val longParam = new ToQueryParam[Long] {
    override def encode(name: String, a: Long): QueryParam =
      StringParam(name, a.toString)
  }
  implicit val booleanParam = new ToQueryParam[Boolean] {
    override def encode(name: String, a: Boolean): QueryParam =
      StringParam(name, a.toString)
  }
  implicit def idOrKeyParam[A] = new ToQueryParam[IdOrKeyParam[A]] {
    override def encode(name: String, a: IdOrKeyParam[A]): QueryParam =
      StringParam(name, a.toString)
  }
}

object QueryParam {

  def apply[A: ToQueryParam](name: String, value: A): QueryParam = {
    val toQueryParam = implicitly[ToQueryParam[A]]
    toQueryParam.encode(name, value)
  }

  def option[A: ToQueryParam](name: String, optValue: Option[A]): QueryParam = {
    val toQueryParam = implicitly[ToQueryParam[A]]
    optValue.map(value => toQueryParam.encode(name, value)).getOrElse(EmptyParam)
  }

  def single[A: ToQueryParam](name: String, value: A): Seq[QueryParam] = {
    val toQueryParam = implicitly[ToQueryParam[A]]
    Seq(toQueryParam.encode(name, value))
  }

  def encodeAll(params: Seq[QueryParam]): Map[String, String] =
    params.foldLeft(Map.empty[String, String]) {
      case (acc, param) =>
        acc ++ param.encode
    }
}