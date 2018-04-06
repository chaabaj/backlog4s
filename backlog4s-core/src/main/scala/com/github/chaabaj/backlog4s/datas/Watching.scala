package com.github.chaabaj.backlog4s.datas

import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas.WatchingSort.WatchingSort
import org.joda.time.DateTime

object WatchingT {
  def id(value: Int): Id[Watching] = Id(value)
}

case class Watching(
  id: Id[Watching],
  resourceAlreadyRead: Boolean,
  note: String,
  `type`: String,
  issue: Issue,
  lastContentUpdated: DateTime,
  created: DateTime,
  updated: Option[DateTime]
)

object WatchingSort extends Enumeration {
  type WatchingSort = Value

  val Created = Value("created")
  val Updated = Value("updated")
  val IssueUpdated = Value("issueUpdated")
}

case class WatchingSearch(
  order: Order = Order.Desc,
  sort: WatchingSort = WatchingSort.IssueUpdated,
  count: Long = 20,
  offset: Long = 0,
  resourceAlreadyRead: Option[Boolean] = None,
  issueId: Option[Seq[Id[Issue]]] = None
)

case class AddWatchingForm(
  issueIdOrKey: IdOrKeyParam[Issue],
  note: Option[String] = None
)
