package com.services

import com.entities.Event
import com.entities.EventType._

import scala.util.parsing.combinator.RegexParsers

object EventParserService extends RegexParsers {
  private val sequence = "\\d+".r ^^ {_.toLong}
  private val userId = "\\w+".r
  private val eventSymbol = "\\w+".r
  private val userIdNoPipe = "|" ~> userId
  private val SequenceNoPipe = sequence <~ "|"
  private val optFromUserId = opt(userIdNoPipe)

  private val totalEvent = SequenceNoPipe ~ eventSymbol ~ optFromUserId ~ optFromUserId ^^ {
    case seq ~ event ~ fromUserId ~ toUserId => Event(seq, event.getOrElse(UNKNNOWN),fromUserId.getOrElse(""),toUserId.getOrElse(""))
  }

  def parseEvent(event: String) = parseAll(totalEvent, event).getOrElse(Event(Long.MinValue,UNKNNOWN,"","", "")).copy(rawData = event)
}
