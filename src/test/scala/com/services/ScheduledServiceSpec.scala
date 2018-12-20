
package com.services

import java.net.Socket

import com.fixtures.EventFixture._
import com.repository.{EventRepository, UserRepository}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

class ScheduledServiceSpec extends FlatSpec with BeforeAndAfter with  Matchers with MockitoSugar {

  val mockUserRepository = mock[UserRepository]
  val realEventRepository = new EventRepository
  val mockSocket = mock[Socket]

  before {
    reset(mockUserRepository)
  }

  class ScheduledServiceMock extends ScheduledService(mockUserRepository,realEventRepository) {
    override val sendEventService = mock[SendEventService]
  }

  "follow" should "be done successfully" in new ScheduledServiceMock {
    when(mockUserRepository.userSocket(any())).thenReturn(Some(mockSocket))
    follow(eventFollow)

    verify(sendEventService).sendEventToSingleSocket(any())(any())
    verify(mockUserRepository).addFollower(any(),any())
    verify(mockUserRepository).userSocket(any())
   }

  "unfollow" should "be done successfully" in new ScheduledServiceMock {
    unfollow(eventFollow)

    verify(mockUserRepository).removeFollower(any(),any())
  }

  "broadcast" should "be done successfully" in new ScheduledServiceMock {
    when(mockUserRepository.userSockets()).thenReturn(Seq(mockSocket))
    broadcast(eventFollow)

    verify(sendEventService).sendEventToSpecificSockets(any(),any())
    verify(mockUserRepository).userSockets()
  }

  "private message" should "be done successfully" in new ScheduledServiceMock {
    when(mockUserRepository.userSocket(any())).thenReturn(Some(mockSocket))
    privateMsg(eventFollow)

    verify(sendEventService).sendEventToSingleSocket(any())(any())
    verify(mockUserRepository).userSocket(any())
  }

  "status update" should "be done successfully" in new ScheduledServiceMock {
    when(mockUserRepository.followersSockets(any())).thenReturn(Vector(mockSocket))
    statusUpdate(eventFollow)

    verify(sendEventService).sendEventToSpecificSockets(any(),any())
    verify(mockUserRepository).followersSockets(any())
  }

  "run" should "be done successfully" in new ScheduledServiceMock {
    realEventRepository.addEvent(eventRawBySequence(21))
    realEventRepository.addEvent(eventRawBySequence(22))
    when(mockUserRepository.userSocket(any())).thenReturn(Some(mockSocket))
    sendEventsTask.run()

    realEventRepository.parsedSortedEvents() should be (List.empty)

    verify(sendEventService, times(2)).sendEventToSingleSocket(any())(any())
    verify(mockUserRepository, times(2)).addFollower(any(),any())
    verify(mockUserRepository, times(2)).userSocket(any())
  }
}