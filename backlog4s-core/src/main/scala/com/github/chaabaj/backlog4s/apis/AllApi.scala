package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.Credentials
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl

class AllApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  lazy val activityApi = new ActivityApi(baseUrl, credentials)
  lazy val attachmentApi = new AttachmentApi(baseUrl, credentials)
  lazy val categoryApi = new CategoryApi(baseUrl, credentials)
  lazy val customStatusApi = new CustomStatusApi(baseUrl, credentials)
  lazy val gitApi = new GitApi(baseUrl, credentials)
  lazy val groupApi = new GroupApi(baseUrl, credentials)
  lazy val issueApi = new IssueApi(baseUrl, credentials)
  lazy val issueCommentApi = new IssueCommentApi(baseUrl, credentials)
  lazy val issueTypeApi = new IssueTypeApi(baseUrl, credentials)
  lazy val milestoneApi = new MilestoneApi(baseUrl, credentials)
  lazy val notificationApi = new NotificationApi(baseUrl, credentials)
  lazy val priorityApi = new PriorityApi(baseUrl, credentials)
  lazy val projectApi = new ProjectApi(baseUrl, credentials)
  lazy val pullRequestCommentApi = new PullRequestCommentApi(baseUrl, credentials)
  lazy val resolutionApi = new ResolutionApi(baseUrl, credentials)
  lazy val sharedFileApi = new SharedFileApi(baseUrl, credentials)
  lazy val spaceApi = new SpaceApi(baseUrl, credentials)
  lazy val starApi = new StarApi(baseUrl, credentials)
  lazy val statusApi = new StatusApi(baseUrl, credentials)
  lazy val userApi = new UserApi(baseUrl, credentials)
  lazy val watchingApi = new WatchingApi(baseUrl, credentials)
  lazy val webhookApi = new WebhookApi(baseUrl, credentials)
  lazy val wikiApi = new WikiApi(baseUrl, credentials)
}

