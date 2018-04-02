package com.github.backlog4s.datas

import com.github.backlog4s.datas.Lang.Lang
import java.time.ZoneId

import org.joda.time.{DateTime, LocalTime}

case class Space(
  spaceKey: Key[Space],
  name: String,
  ownerId: Id[User],
  lang: Option[Lang] = Some(Lang.English),
  timezone: ZoneId,
  reportSendTime: LocalTime,
  textFormattingRule: String,
  created: DateTime,
  updated: Option[DateTime]
)

case class SpaceNotification(
  content: String,
  updated: Option[DateTime]
)

case class ProjectDiskUsage(
  projectId: Id[Project],
  issue: Long,
  wiki: Long,
  file: Long,
  subversion: Long,
  git: Long
)

case class SpaceDiskUsage(
  capacity: Long,
  issue: Long,
  wiki: Long,
  file: Long,
  subversion: Long,
  git: Long,
  details: Seq[ProjectDiskUsage]
)

// What is space notification
