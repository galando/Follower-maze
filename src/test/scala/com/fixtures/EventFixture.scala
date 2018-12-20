package com.fixtures

import com.entities.Event
import com.entities.EventType._


object EventFixture {

  def eventRawBySequence(sequence: Long) = s"$sequence|F|1|2"

  def eventBySequence(sequence: Long) = Event(sequence,FOLLOW,"1","2",eventRawBySequence(sequence))

  def eventRawFollow = s"123|F|1|2"
  def eventFollow = Event(123,FOLLOW,"1","2",eventRawFollow)
  def eventRawUnFollow = s"123|U|1|2"
  def eventUnFollow = Event(123,UNFOLLOW,"1","2",eventRawUnFollow)
  def eventRawBroadcast = s"123|B"
  def eventBroadcast = Event(123,BROADCAST,"","",eventRawBroadcast)
  def eventRawPrivateMsg = s"123|P|1|2"
  def eventPrivateMsg = Event(123,PRIVATE_MSG,"1","2",eventRawPrivateMsg)
  def eventRawStatusUpdate = s"123|S|1"
  def eventStatusUpdate = Event(123,STATUS_UPDATE,"1","",eventRawStatusUpdate)
  def eventRawUnknown = s"${Long.MinValue}|X|1"
  def eventUnknown = Event(Long.MinValue,UNKNNOWN,"","",eventRawUnknown)
}
