package com.entities

import com.entities.EventType._

case class Event (sequence: Long, eventType: EventType, fromUserId: String, toUserId: String, rawData: String = "") extends Ordered[Event] {
  def compare(event: Event) = sequence.compareTo(event.sequence)
}