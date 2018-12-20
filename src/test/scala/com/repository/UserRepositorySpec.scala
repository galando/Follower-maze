package com.repository

import java.net.Socket

import org.scalatest._
import org.scalatest.mock.MockitoSugar

class UserRepositorySpec extends FlatSpec with Matchers with MockitoSugar {

  val userId1 = "userId1"
  val userId2 = "userId2"
  trait UserRepositoryMock extends UserRepository

  "userSockets" should "be matched successfully" in new UserRepositoryMock() {
    val mockSocket1 = mock[Socket]
    mockSocket1.setKeepAlive(true)
    val mockSocket2 = mock[Socket]
    mockSocket1.setKeepAlive(false)
    addUserSocket(userId1, mockSocket1)
    addUserSocket(userId2, mockSocket2)
    val result = userSockets().toList
    result.size should equal(2)
    result should contain allOf(mockSocket1,mockSocket2)
  }

  "userSocket" should "be found successfully" in new UserRepositoryMock() {
    val mockSocket1 = mock[Socket]
    mockSocket1.setKeepAlive(true)
    addUserSocket(userId1, mockSocket1)
    userSocket(userId1) should be (Some(mockSocket1))
  }

  "userSocket" should "not be found successfully" in new UserRepositoryMock() {
    val mockSocket1 = mock[Socket]
    mockSocket1.setKeepAlive(true)
    addUserSocket(userId1, mockSocket1)
    userSocket(userId2) should be (None)
  }

  "followersSockets" should "not be found successfully - another user exists" in new UserRepositoryMock() {
    val mockSocket = mock[Socket]
    addUserSocket(userId1, mockSocket)
    addFollower(userId1,userId2)
    val result = followersSockets(userId1)
    result.size should be (0)
  }

  "followersSockets" should "be found successfully - no user exists" in new UserRepositoryMock() {
    val mockSocket = mock[Socket]
    addFollower(userId1,userId2)
    val result = followersSockets(userId1)
    result.size should be (0)
  }

  "followersSockets" should "be found successfully" in new UserRepositoryMock() {
    val mockSocket = mock[Socket]
    addUserSocket(userId1, mockSocket)
    addUserSocket(userId2, mockSocket)
    addFollower(userId1,userId2)
    val result = followersSockets(userId1)
    result.size should be (1)
    result.head should be (mockSocket)
  }

  "removeFollower" should "remove follower successfully - follower exists" in new UserRepositoryMock() {
    val mockSocket = mock[Socket]
    addUserSocket(userId1, mockSocket)
    addUserSocket(userId2, mockSocket)
    addFollower(userId1,userId2)
    removeFollower(userId1,userId2)
    val result = followersSockets(userId1)
    result.size should be (0)
  }

  "removeFollower" should "do nothing - follower doesn't exist" in new UserRepositoryMock() {
    val mockSocket = mock[Socket]
    addFollower(userId1,userId2)
    removeFollower(userId1,userId2)
    val result = followersSockets(userId1)
    result.size should be (0)
  }

}