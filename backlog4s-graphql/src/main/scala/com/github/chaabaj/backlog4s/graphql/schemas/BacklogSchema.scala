package com.github.backlog4s.graphql.schemas

import sangria.schema.ObjectType

trait BacklogSchema[T, U] {
  def schema: ObjectType[T, U]
}
