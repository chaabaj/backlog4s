package backlog4s.datas

import org.joda.time.DateTime

case class Star(
  id: Id[Star],
  comment: Option[String],
  url: String,
  title: String,
  presenter: User,
  created: DateTime
)
