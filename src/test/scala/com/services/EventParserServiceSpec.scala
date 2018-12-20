package com.services

import com.fixtures.EventFixture._
import org.scalatest._

class EventParserServiceSpec extends FlatSpec with Matchers {

   "Events of all event types" should "be matched successfully" in {
     EventParserService.parseEvent(eventRawFollow) should be (eventFollow)
     EventParserService.parseEvent(eventRawUnFollow) should be (eventUnFollow)
     EventParserService.parseEvent(eventRawBroadcast) should be (eventBroadcast)
     EventParserService.parseEvent(eventRawPrivateMsg) should be (eventPrivateMsg)
     EventParserService.parseEvent(eventRawStatusUpdate) should be (eventStatusUpdate)
     EventParserService.parseEvent(eventRawUnknown) should be (eventUnknown)
     EventParserService.parseEvent("122") should be (eventUnknown.copy(rawData = "122"))
   }
 }