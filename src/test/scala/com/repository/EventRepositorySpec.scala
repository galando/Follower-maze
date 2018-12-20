package com.repository

import com.fixtures.EventFixture._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

class EventRepositorySpec extends FlatSpec with Matchers with MockitoSugar {

  trait EventRepositoryMock extends EventRepository

  "parseSortedEvents" should "be matched successfully - no events exists" in new EventRepositoryMock() {
    parsedSortedEvents().size should be (0)
  }

  "parseSortedEvents" should "be matched successfully - one event exist" in new EventRepositoryMock() {
    addEvent(eventRawFollow)
    val result = parsedSortedEvents()
    result.size should be (1)
    result.head should be (eventFollow)
  }

  "removeEvent" should "remove event successfully" in new EventRepositoryMock() {
    addEvent(eventRawFollow)
    removeEvent(eventRawFollow)
    parsedSortedEvents().size should be (0)
  }
  "removeEvent" should "do nothing - event doesn't exist" in new EventRepositoryMock() {
    removeEvent(eventRawFollow)
    parsedSortedEvents().size should be (0)
  }

  "parseSortedEvents" should "be matched successfully - sorted events" in new EventRepositoryMock() {
    addEvent(eventRawBySequence(123))
    addEvent(eventRawBySequence(21))
    addEvent(eventRawBySequence(432))
    val result = parsedSortedEvents()
    result.size should be (3)
    result.head should be (eventBySequence(21))
    result(1) should be (eventBySequence(123))
    result(2) should be (eventBySequence(432))
  }
}