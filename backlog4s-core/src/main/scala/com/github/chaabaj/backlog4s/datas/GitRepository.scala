package com.github.chaabaj.backlog4s.datas

import org.joda.time.DateTime

object GitRepositoryT {
  def id(value: Long): Id[GitRepository] = Id(value)
}

case class GitRepository(
  id: Id[GitRepository],
  projectId: Id[Project],
  name: String,
  description: Option[String],
  hookUrl: Option[String],
  httpUrl: Option[String],
  sshUrl: Option[String],
  pushedAt: Option[String],
  displayOrder: Long,
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime]
)