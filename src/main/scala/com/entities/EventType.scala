package com.entities

import scala.language.implicitConversions

object EventType {
  sealed trait EventType {
    val value: String
  }

  case object FOLLOW extends EventType {
    override val value: String = "F"
  }
  case object UNFOLLOW extends EventType {
    override val value: String = "U"
  }
  case object BROADCAST extends EventType {
    override val value: String = "B"
  }
  case object PRIVATE_MSG extends EventType {
    override val value: String = "P"
  }
  case object STATUS_UPDATE extends EventType {
    override val value: String = "S"
  }
  case object UNKNNOWN extends EventType {
    override val value: String = "UNKNNOWN"
  }

  val values = FOLLOW::UNFOLLOW::BROADCAST::PRIVATE_MSG::STATUS_UPDATE::Nil

  values.map(_.value)

  implicit def eventTypeToString(event: String): Option[EventType] = values.find(_.value == event)
}
