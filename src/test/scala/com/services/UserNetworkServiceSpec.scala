
package com.services

import java.io.BufferedReader
import java.net.{ServerSocket, Socket, SocketTimeoutException}

import com.fixtures.EventFixture._
import com.repository.UserRepository
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

class UserNetworkServiceSpec extends FlatSpec with Matchers with MockitoSugar {

  val NUM_OF_USERS = 10
  val realUserRepository = new UserRepository

  class UserNetworkServiceMock extends UserNetworkService(realUserRepository) {
    val mockServerSocket = mock[ServerSocket]
    val mockSocket = mock[Socket]
    val mockReader = mock[BufferedReader]

    val mockSockets = for (i <- 2 to NUM_OF_USERS) yield mockSocket
    val usersIdToMockTo = for (i <- 2 to NUM_OF_USERS) yield s"$i"

    when(mockServerSocket.accept()).thenReturn(mockSocket,mockSockets :_*).thenThrow(new SocketTimeoutException("No new connections"))

    override lazy val userServerSocket = mockServerSocket
    override def createReader(socket: Socket) = mockReader

    when(mockReader.readLine()).thenReturn("1",usersIdToMockTo :_*).thenReturn(null)
  }

  "read and parse events" should "be done successfully" in new UserNetworkServiceMock {
    val eventsToVerify = (for (i <- 1 to NUM_OF_USERS) yield eventBroadcast).toList
    run()
    realUserRepository.userSockets().size should be (NUM_OF_USERS)
   }
 }