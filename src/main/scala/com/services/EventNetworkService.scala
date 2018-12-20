package com.services

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.ServerSocket

import com.repository.{UserRepository, EventRepository}


class EventNetworkService(userRepository: UserRepository) extends Thread {

  val eventsRepository = new EventRepository
  private val eventServerSocket = new ServerSocket(9090)
  private val scheduledService = new ScheduledService(userRepository, eventsRepository)

  def readerSocket() = new BufferedReader(new InputStreamReader(eventServerSocket.accept().getInputStream))

  override def run() = {
    try {
      eventServerSocket.setSoTimeout(10000)

      val bufferedReader = readerSocket()
      Iterator.continually(bufferedReader.readLine).takeWhile(_ != null).foreach(eventsRepository.addEvent)
    }
    catch {
      case e: IOException => // Ignore exception - exits the while loop
    }
    finally {
      closedResources()
    }
  }

  def closedResources() = {
    eventServerSocket.close()
    scheduledService.closeResources()
    userRepository.closeResources()
  }
}
