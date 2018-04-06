package com.github.chaabaj.backlog4s.datas

import org.joda.time.DateTime

case class SharedFile(
  id: Id[SharedFile],
  `type`: String,
  dir: String,
  name: String,
  size: Int,
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime]
)

case class LinkFilesForm(
  fileId: Seq[Id[SharedFile]]
)
