# Back-end Developer Challenge: Follower Maze - Solution Documentation

## The Components

- **UserRepository**- This class saves the mapping between:
    1) Users' id and their socket.
    2) Users' id and their Vector of followers (which has tuples of userId and Option[Socket] -
        because this follower's socket might not exist yet)
    I choose TrieMap data structure, which is concurrent thread-safe lock-free implementation of a hash array mapped trie.

- **EventRepository** - This class save all events which was received by the **EventNetworkService** -
                        in every 2 seconds the repository removes the events (by **ScheduledService**) -
                        which means that the server doesn't save all events - only a snapshot of the last 2 seconds.
    I choose ListBuffer, which provides constant time of append and converts this buffer to a list in constant time as well.
    It's thread safe in our case, because one thread appends new event to the end of the buffer while the other gets
    the list - in the worst case the appended event will be taken in the next trigger of the **ScheduledService**.
    In addition, the thread who gets the list also removes the entries - no other thread is going to use this entry, so
    this action is also thread safe.

- **Event** - A case class who keeps the content of the events into attributes, and extends Ordered for sorting.

- **EventType** - The Event Type - implemented using sealed trait - so the compile will warn during pattern matching
                                   if the match isn't exhaustive.

- **EventParserService** - This object parsed the message using RegexParsers.
                           This is the reason I add ("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2")
                           to the build.sbt. It's still part of the standard library of scala.

- **SendEventService** - This class implements methods for sending the event to single or more sockets.

- **UserNetworkService** - This class implements Thread. When executed:
                           1) Waits for a connection to be made.
                           2) Reads the message (using readLine, because the message ends with "\r\n"
                           3) Adds the message to the user repository (using **UserRepository**)
                           The executions of this thread is finished when there is a timeout at the socket -
                           an exception is thrown.
                           The socket has timeout of 5 seconds.

- **EventNetworkService** - This class implements Thread. When executed:
                            1) Waits for a connection to be made.
                            2) Create read buffer for reading the messages from the socket.
                            3) Creates an infinite-length iterator returning the results of reading from the socket.
                            It will reads till it null arrives - which means no messages are been sent anymore.
                            Link of further explanation: http://docs.scala-lang.org/tutorials/FAQ/stream-view-iterator.html
                            4) During iteration, adds the message to the event repository (using **EventRepository**)
                            The executions of this thread is finished null message arrives.
                            The socket has timeout of 10 seconds.

- **ScheduledService** - This class has scheduled executor of fixed num of thread
                         (which equals to the available processes of the machine)
                         The scheduler is triggered in every 2 seconds.
                         In each execution:
                         1) Retrieves the sorted parsed events (**Event**). This is how the server always keeps the
                            correct order of the events.
                            If the **maxEventSourceBatchSize** has more messages which been sent in more than 2
                            seconds - then the trigger of the scheduler should be bigger.
                         2) For each parsed event it execute the expected behaviour (there are methods for each
                            **Event Type**)
                         3) Remove the parsed event using **EventRepository**

## Testing
- There are unit tests for all components, both positive and negative.
- I added mockito and junit modules for testing.
- Please run "sbt test" to execute them.

## Execution
- Run "sbt run" and after you see "Running Main" please execute "./followermaze.sh"
- The server running and listening to ports 9090 and 9099, as requested.