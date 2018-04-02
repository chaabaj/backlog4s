package com.github.chaabaj.backlog4s.datas

case class Attachment(
  id: Id[Attachment],
  name: String,
  size: Long
)

case class AttachForm(
  attachmentId: Seq[Id[Attachment]]
)