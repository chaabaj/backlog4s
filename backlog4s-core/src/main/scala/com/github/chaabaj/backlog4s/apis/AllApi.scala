package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.Credentials

class AllApi(override val baseUrl: String,
             override val credentials: Credentials) extends Api {

  lazy val activityApi = ActivityApi(baseUrl, credentials)
  lazy val attachmentApi = AttachmentApi(baseUrl, credentials)
  lazy val categoryApi = CategoryApi(baseUrl, credentials)
  lazy val gitApi = GitApi(baseUrl, credentials)
  lazy val groupApi = GroupApi(baseUrl, credentials)
  lazy val issueApi = IssueApi(baseUrl, credentials)
  lazy val issueCommentApi = IssueCommentApi(baseUrl, credentials)
  lazy val issueTypeApi = IssueTypeApi(baseUrl, credentials)
  lazy val milestoneApi = MilestoneApi(baseUrl, credentials)
  lazy val notificationApi = NotificationApi(baseUrl, credentials)
  lazy val priorityApi = PriorityApi(baseUrl, credentials)
  lazy val projectApi = ProjectApi(baseUrl, credentials)
  lazy val pullRequestCommentApi = PullRequestCommentApi(baseUrl, credentials)
  lazy val resolutionApi = ResolutionApi(baseUrl, credentials)
  lazy val sharedFileApi = SharedFileApi(baseUrl, credentials)
  lazy val spaceApi = SpaceApi(baseUrl, credentials)
  lazy val starApi = StarApi(baseUrl, credentials)
  lazy val statusApi = StatusApi(baseUrl, credentials)
  lazy val userApi = UserApi(baseUrl, credentials)
  lazy val watchingApi = WatchingApi(baseUrl, credentials)
  lazy val webhookApi = WebhookApi(baseUrl, credentials)
  lazy val wikiApi = WikiApi(baseUrl, credentials)
}

object AllApi extends ApiContext[AllApi] {
  override def apply(baseUrl: String, credentials: Credentials): AllApi =
    new AllApi(baseUrl, credentials)
}
