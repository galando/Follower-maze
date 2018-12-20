import com.repository.UserRepository
import com.services._


object Main  {
    def main(args: Array[String]) {

      val userRepository = new UserRepository

      new UserNetworkService(userRepository).start()
      new EventNetworkService(userRepository).start()
    }
}


