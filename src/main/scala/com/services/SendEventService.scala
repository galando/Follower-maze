package com.services

import java.io.PrintStream
import java.net.Socket

import scala.collection.parallel.immutable.ParVector


class SendEventService {

  def sendEventToSingleSocket(event : String)(socket: Socket) = {
    val printStream = new PrintStream(socket.getOutputStream)
    printStream.println(event)
    printStream.flush()
  }

  def sendEventToSpecificSockets(event: String, sockets: ParVector[Socket]) = sockets.foreach(sendEventToSingleSocket(event))
}
