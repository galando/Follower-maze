package com.services

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.{ServerSocket, Socket}

import com.repository.UserRepository


class UserNetworkService(userRepository: UserRepository) extends Thread {

  lazy val userServerSocket = new ServerSocket(9099)

  def createReader(socket: Socket) = new BufferedReader(new InputStreamReader(socket.getInputStream))

  override def run() = {
    try {
      userServerSocket.setSoTimeout(5000)

      while (true) {
        val socket = userServerSocket.accept()
        val reader = createReader(socket)
        val event = reader.readLine()
        if (event != null) {
          userRepository.addUserSocket(event, socket)
        }
      }
    }
    catch {
      case e: IOException => // Ignore exception - exits the while loop
    }
  }
}
