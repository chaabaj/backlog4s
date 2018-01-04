package backlog4s.datas

import backlog4s.datas.ActivityType.ActivityType
import org.joda.time.DateTime

object WebhookT {
  def id(value: Long): Id[Webhook] = Id(value)
}

case class Webhook(
  id: Id[Webhook],
  name: String,
  description: Option[String],
  hookUrl: String,
  allEvent: Boolean,
  activityTypeIds: Seq[ActivityType],
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime]
)

case class AddWebhookForm(
  name: String,
  description: String,
  hookUrl: String,
  allEvent: Boolean = false,
  activityTypeIds: Seq[ActivityType]
)

case class UpdateWebhookForm(
  name: Option[String] = None,
  description: Option[String] = None,
  hookUrl: Option[String] = None,
  allEvent: Option[Boolean] = None,
  activityTypeIds: Option[Seq[ActivityType]] = None
)