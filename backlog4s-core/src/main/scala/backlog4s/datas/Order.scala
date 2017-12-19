package backlog4s.datas

object Order extends Enumeration {
  type Order = Value
  val Asc = Value("asc")
  val Desc = Value("desc")
}