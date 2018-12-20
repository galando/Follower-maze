package com.repository

import java.net.Socket

import scala.collection.concurrent.TrieMap

class UserRepository {
  private val userIdSocketMap = new TrieMap[String, Socket]
  private val userFollowers = new TrieMap[String, Vector[(String, Option[Socket])]]

  def userSockets() = userIdSocketMap.values

  def userSocket(userId: String) = userIdSocketMap.get(userId)

  def followersSockets(fromUserId: String) = userFollowers.getOrElse(fromUserId, Vector.empty).flatMap{case (_, socket) => socket}

  def addUserSocket(fromUserId: String, socket: Socket) = userIdSocketMap.put(fromUserId, socket)

  def addFollower(toUserId: String, fromUserId: String) = userFollowers.update(toUserId, userFollowers.getOrElse(toUserId, Vector.empty) :+(fromUserId, userSocket(fromUserId)))
  
  def removeFollower(toUserId: String, fromUserId: String) = userFollowers.update(toUserId, userFollowers.getOrElse(toUserId, Vector.empty).filterNot { case (userId, _) => userId == fromUserId })

  def closeResources() = userSockets().foreach(_.close())
}