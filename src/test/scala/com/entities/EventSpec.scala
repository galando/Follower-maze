package com.entities

import com.fixtures.EventFixture._
import org.scalatest._

class EventSpec extends FlatSpec with Matchers {

  "sorted events" should "be ascending" in {
    val event1 = eventBySequence(123)
    val event2 = eventBySequence(234)
    val event3 = eventBySequence(12)
    val seq = Seq(event1,event2,event3).sorted
    seq.head should be (event3)
    seq(1) should be (event1)
    seq(2) should be (event2)
  }
}