package com.repository

import com.services.EventParserService

import scala.collection.mutable.ListBuffer


class EventRepository {

  private val eventsToSend = new ListBuffer[String]

  def addEvent(event: String) = eventsToSend+= event

  def removeEvent(event: String) = eventsToSend-= event

  def parsedSortedEvents() = eventsToSend.toList.map(EventParserService.parseEvent).sorted
}
