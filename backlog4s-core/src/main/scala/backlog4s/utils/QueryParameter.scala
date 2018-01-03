package backlog4s.utils

object QueryParameter {

  def removeEmptyValue(params: Map[String, String]): Map[String, String] =
    params.filter {
      case (_, value) if value.nonEmpty => true
      case _ => false
    }
}
