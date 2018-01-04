package backlog4s.formatters

import java.time.ZoneId

import backlog4s.datas._
import org.joda.time.{DateTime, LocalTime}
import org.joda.time.format.ISODateTimeFormat
import spray.json._

import scala.util.Try

/**
  * Define Enum type for encoding and decoding json
  * It tell how to format enumeration in json
  * If it should be a string or a number
  */
sealed trait EnumType
case object IntEnum extends EnumType
case object StringEnum extends EnumType

object SprayJsonFormats extends DefaultJsonProtocol {

  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {

    val formatter = ISODateTimeFormat.dateOptionalTimeParser()

    def write(obj: DateTime): JsValue =
      JsString(DateTimeFormat.formatter.print(obj))

    def read(json: JsValue): DateTime = json match {
      case JsString(s) =>
        Try(formatter.parseDateTime(s))
          .getOrElse(error(s))
      case _ =>
        error(json.toString())
    }

    def error(v: Any): DateTime = {
      val example = formatter.print(0)
      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
    }
  }

  implicit object LocalTimeFormat extends RootJsonFormat[LocalTime] {

    override def read(json: JsValue): LocalTime = json match {
      case JsString(time) =>
        Try(LocalTime.parse(time))
          .getOrElse(deserializationError(s"$time is not a valid time value"))
      case _ =>
        deserializationError(s"$json is not a valid time value")
    }

    override def write(time: LocalTime): JsValue = JsString(time.toString("hh:mm:ss"))
  }

  class EnumFormat[E <: Enumeration](enu: E, enumType: EnumType) extends RootJsonFormat[E#Value] {
    override def read(json: JsValue): E#Value = json match {
      case JsNumber(num) => enu.apply(num.toInt)
      case JsString(name) => enu.withName(name)
      case _ => throw DeserializationException(s"Unexpected input ${json.prettyPrint}")
    }

    override def write(obj: E#Value): JsValue = enumType match {
      case IntEnum => JsNumber(obj.id)
      case StringEnum => JsString(obj.toString)
    }
  }

  class IdFormat[A]() extends RootJsonFormat[Id[A]] {
    override def read(json: JsValue): Id[A] = json match {
      case JsNumber(idVal) => Id(idVal.toLong)
      case _ =>
        throw DeserializationException(s"Expected a js number got ${json.prettyPrint}")
    }

    override def write(obj: Id[A]): JsValue = JsNumber(obj.value)
  }

  class KeyFormat[A]() extends RootJsonFormat[Key[A]] {
    override def read(json: JsValue): Key[A] = json match {
      case JsString(keyVal) => Key(keyVal)
      case _ =>
        throw DeserializationException(s"Expected a js string got ${json.prettyPrint}")
    }

    override def write(obj: Key[A]): JsValue = JsString(obj.value)
  }

  class IdOrKeyFormat[A]() extends RootJsonFormat[IdOrKeyParam[A]] {
    override def read(json: JsValue): IdOrKeyParam[A] = json match {
      case JsNumber(idVal) => IdParam[A](Id[A](idVal.toLong))
      case JsString(keyVal) => KeyParam[A](Key[A](keyVal))
      case _ => deserializationError(s"Expected a string or a number for IdOrKeyParam got ${json.prettyPrint}")
    }

    override def write(idOrKey: IdOrKeyParam[A]): JsValue = JsString(idOrKey.toString)
  }

  implicit object ZoneFormat extends RootJsonFormat[ZoneId] {
    override def read(json: JsValue): ZoneId = json match {
      case JsString(zone) => ZoneId.of(zone)
      case _ =>
        throw DeserializationException(s"Expected a timezone got ${json.prettyPrint}")
    }
    override def write(zone: ZoneId): JsValue = JsString(zone.toString)
  }

  implicit object RGBColorFormat extends RootJsonFormat[RGBColor] {
    override def read(json: JsValue): RGBColor = json match {
      case JsString(hexColor) =>
        Color.fromHex(hexColor)
          .getOrElse(deserializationError(s"not a valid color got $hexColor expected format #RRGGBB"))
      case other =>
        deserializationError(s"Color must be a string got ${other.prettyPrint}")
    }

    override def write(color: RGBColor): JsValue = JsString(color.toHex)
  }

  implicit val userIdFormat = new IdFormat[User]
  implicit val roleFormat = new EnumFormat(Role, IntEnum)
  implicit val langFormat = new EnumFormat(Lang, StringEnum)
  implicit val errorCodeFormat = new EnumFormat(ApiErrorCode, IntEnum)
  implicit val orderFormat = new EnumFormat(Order, StringEnum)
  implicit val errorFormat = jsonFormat3(ApiError)
  implicit val errorsFormat = jsonFormat1(ApiErrors)

  implicit val userFormat: JsonFormat[User] = jsonFormat6(User)
  implicit val addUserFormFormat = jsonFormat5(AddUserForm)
  implicit val updateUserFormFormat = jsonFormat4(UpdateUserForm)

  implicit val idGroupFormat = new IdFormat[Group]
  implicit val group = jsonFormat8(Group)
  implicit val addGroupFormFormat = jsonFormat1(AddGroupForm)
  implicit val updateGroupFormFormat = jsonFormat2(UpdateGroupForm)

  implicit val idProjectFormat = new IdFormat[Project]
  implicit val keyProjectFormat = new KeyFormat[Project]
  implicit val projectFormat = jsonFormat8(Project)
  implicit val addProjectFormFormat = jsonFormat5(AddProjectForm)
  implicit val updateProjectFormFormat = jsonFormat7(UpdateProjectForm)

  implicit val spaceIdFormat = new KeyFormat[Space]
  implicit val spaceFormat = jsonFormat9(Space)
  implicit val spaceNotificationFormat = jsonFormat2(SpaceNotification)
  implicit val projectDiskUsageFormat = jsonFormat6(ProjectDiskUsage)
  implicit val spaceDiskUsage = jsonFormat7(SpaceDiskUsage)

  implicit val idCategoryFormat = new IdFormat[Category]
  implicit val categoryFormat = jsonFormat3(Category)

  implicit val idMilestoneFormat = new IdFormat[Milestone]
  implicit val milestoneFormat = jsonFormat8(Milestone)
  implicit val addMilestoneFormFormat = jsonFormat4(AddMilestoneForm)
  implicit val updateMilestoneFormFormat = jsonFormat5(UpdateMilestoneForm)

  implicit val idIssueTypeFormat = new IdFormat[IssueType]
  implicit val idOrKeyIssueFormat = new IdOrKeyFormat[Issue]
  implicit val issueTypeFormat = jsonFormat5(IssueType)
  implicit val updateIssueTypeFormFormat = jsonFormat2(UpdateIssueTypeForm)
  implicit val idStatusFormat = new IdFormat[Status]
  implicit val statusFormat = jsonFormat2(Status)
  implicit val idResolutionFormat = new IdFormat[Resolution]
  implicit val resolutionFormat = jsonFormat2(Resolution)
  implicit val idPriorityFormat = new IdFormat[Priority]
  implicit val priorityFormat = jsonFormat2(Priority)
  implicit val idAttachmentFormat = new IdFormat[Attachment]
  implicit val attachmentFormat = jsonFormat3(Attachment)
  implicit val attachFormFormat = jsonFormat1(AttachForm)
  implicit val idSharedFileFormat = new IdFormat[SharedFile]
  implicit val sharedFileFormat = jsonFormat9(SharedFile)
  implicit val linkFilesForm = jsonFormat1(LinkFilesForm)
  implicit val idStarFormat = new IdFormat[Star]
  implicit val starFormat = jsonFormat6(Star)
  implicit val idIssueFormat = new IdFormat[Issue]
  implicit val issueFormat = jsonFormat19(Issue)
  implicit val countFormat = jsonFormat1(Count)
  implicit val addIssueFormFormat = jsonFormat15(AddIssueForm)
  implicit val updateIssueFormFormat = jsonFormat17(UpdateIssueForm)
  implicit val idCommentFormat = new IdFormat[Comment]
  implicit val idNotificationFormat = new IdFormat[Notification]
  implicit val notificationFormat = jsonFormat5(Notification)
  implicit val addNotificationFormFormat = jsonFormat1(AddNotificationForm)
  implicit val commentFormat = jsonFormat7(Comment)
  implicit val addCommentFormFormat = jsonFormat3(AddCommentForm)
  implicit val activityTypeFormat = new EnumFormat(ActivityType, IntEnum)
  implicit val activityChangeFormat = jsonFormat4(ActivityChange)
  implicit val idActivityCommentFormat = new IdFormat[ActivityComment]
  implicit val activityCommentFormat = jsonFormat2(ActivityComment)
  implicit val idActivityContentFormat = new IdFormat[ActivityContent]
  implicit val activityContentFormat = jsonFormat6(ActivityContent)
  implicit val idActivityFormat = new IdFormat[Activity]
  implicit val activityFormat = jsonFormat7(Activity)

  implicit val idWikiTagFormat = new IdFormat[WikiTag]
  implicit val wikiTagFormat = jsonFormat2(WikiTag)
  implicit val idWikiFormat = new IdFormat[Wiki]
  implicit val wikiSummaryFormat = jsonFormat8(WikiSummary)
  implicit val wikiFormat = jsonFormat12(Wiki)
  implicit val addWikiFormFormat = jsonFormat4(AddWikiForm)
  implicit val updateWikiFormFormat = jsonFormat3(UpdateWikiForm)
  implicit val wikiHistoryFormat = jsonFormat6(WikiHistory)
  implicit val starFormFormat = jsonFormat3(StarForm)

  implicit val idGitRepository = new IdFormat[GitRepository]
  implicit val gitRepository = jsonFormat13(GitRepository)

  implicit val prStatusTypeFormat = new EnumFormat(PRStatusType, StringEnum)
  implicit val idPrStatus = new IdFormat[PullRequestStatus]
  implicit val prStatusFormat = jsonFormat2(PullRequestStatus)
  implicit val prIssue = jsonFormat1(PullRequestIssue)
  implicit val idPullRequest = new IdFormat[PullRequest]
  implicit val prSummaryFormat = jsonFormat19(PullRequestSummary)
  implicit val prFormat = jsonFormat21(PullRequest)
  implicit val addPrFormat = jsonFormat8(AddPullRequestForm)
  implicit val updatePrFormat = jsonFormat6(UpdatePullRequestForm)

  implicit val idWatchingFormat = new IdFormat[Watching]
  implicit val watchingFormat = jsonFormat8(Watching)
  implicit val addWatchingFormFormat = jsonFormat2(AddWatchingForm)

  implicit val idWebhookFormat = new IdFormat[Webhook]
  implicit val webhookFormat = jsonFormat10(Webhook)
  implicit val addWebhookFormFormat = jsonFormat5(AddWebhookForm)
  implicit val updateWebhookFormFormat = jsonFormat5(UpdateWebhookForm)
}
