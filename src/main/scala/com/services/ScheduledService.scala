package com.services

import java.util.concurrent.{Executors, TimeUnit}

import com.entities.Event
import com.entities.EventType._
import com.repository.{EventRepository, UserRepository}

class ScheduledService(userRepository: UserRepository, eventRepository: EventRepository) {

  private val scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime.availableProcessors())
  val sendEventService = new SendEventService

  val sendEventsTask = new Runnable() {
    override def run() = {
      val currentEvents = eventRepository.parsedSortedEvents()
      currentEvents.foreach { eventEntity =>
        eventEntity.eventType match {
          case FOLLOW =>        follow(eventEntity)
          case UNFOLLOW =>      unfollow(eventEntity)
          case BROADCAST =>     broadcast(eventEntity)
          case PRIVATE_MSG =>   privateMsg(eventEntity)
          case STATUS_UPDATE => statusUpdate(eventEntity)
          case UNKNNOWN => // ignoring an event of unknown type
        }
        eventRepository.removeEvent(eventEntity.rawData)
      }
    }
  }

  def follow(eventEntity: Event) = {
    userRepository.addFollower(eventEntity.toUserId, eventEntity.fromUserId)
    userRepository.userSocket(eventEntity.toUserId).foreach(sendEventService.sendEventToSingleSocket(eventEntity.rawData))
  }

  def unfollow(eventEntity: Event) = userRepository.removeFollower(eventEntity.toUserId, eventEntity.fromUserId)

  def broadcast(eventEntity: Event) = sendEventService.sendEventToSpecificSockets(eventEntity.rawData,userRepository.userSockets().toVector.par)

  def privateMsg(eventEntity: Event) = userRepository.userSocket(eventEntity.toUserId).foreach(sendEventService.sendEventToSingleSocket(eventEntity.rawData))

  def statusUpdate(eventEntity: Event) = sendEventService.sendEventToSpecificSockets(eventEntity.rawData,userRepository.followersSockets(eventEntity.fromUserId).par)

  def closeResources() = {
    scheduledExecutor.awaitTermination(5,TimeUnit.SECONDS)
    scheduledExecutor.shutdown()
  }

  scheduledExecutor.scheduleAtFixedRate(sendEventsTask, 1, 2, TimeUnit.SECONDS)
}
