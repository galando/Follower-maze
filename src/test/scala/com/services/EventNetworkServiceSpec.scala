
package com.services

import java.io.BufferedReader
import java.net.Socket

import com.fixtures.EventFixture._
import com.repository.UserRepository
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

class EventNetworkServiceSpec extends FlatSpec with Matchers with MockitoSugar {

  val mockUserRepository = mock[UserRepository]

  "read and parse events" should "be done successfully" in new EventNetworkService(mockUserRepository) {
     val mockReader = mock[BufferedReader]

     override def readerSocket() = mockReader

     val mockSocket = mock[Socket]
     val eventsToMockTo = for (_ <- 1 to 10) yield eventRawBroadcast
     val eventsToVerify = (for (_ <- 1 to 10) yield eventBroadcast).toList
     when(mockReader.readLine()).thenReturn(eventRawFollow,eventsToMockTo :_*).thenReturn(null)

     run()

    eventsRepository.parsedSortedEvents() should be (eventFollow::eventsToVerify)
   }
 }