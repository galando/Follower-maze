# Back-end Developer Challenge: Follower Maze
Thanks for trying our development challenge!

With this document you should have received two other files:

- `followermaze.sh`, an executable bash script
- `FollowerMaze-assembly-2.0.jar`, a JAR file to be executed on a JDK 7 JVM

If you haven't received any of these, or if you think there are any
problems with the files, please contact us immediately and
we will re-send you the missing pieces.

## The Challenge
The challenge proposed here is to build a system which acts as a socket
server, reading events from an *event source* and forwarding them when
appropriate to *user clients*.

Clients will connect through TCP and use the simple protocol described in a
section below. There will be two types of clients connecting to your server:

- **One** *event source*: It will send you a
stream of events which may or may not require clients to be notified
- **Many** *user clients*: Each one representing a specific user,
these wait for notifications for events which would be relevant to the
user they represent

### The Protocol
The protocol used by the clients is string-based (i.e. a `CRLF` control
character terminates each message). All strings are encoded in `UTF-8`.

The *event source* **connects on port 9090** and will start sending
events as soon as the connection is accepted.

The many *user clients* will **connect on port 9099**. As soon
as the connection is accepted, they will send to the server the ID of
the represented user, so that the server knows which events to
inform them of. For example, once connected a *user client* may send down:
`2932\r\n`, indicating that they are representing user 2932.

After the identification is sent, the *user client* starts waiting for
events to be sent to them. Events coming from *event source* should be
sent to relevant *user clients* exactly like read, no modification is
required or allowed.

### The Events
There are five possible events. The table below describe payloads
sent by the *event source* and what they represent:

| Payload       | Sequence #| Type         | From User Id | To User Id |
|---------------|-----------|--------------|--------------|------------|
|666\|F\|60\|50 | 666       | Follow       | 60           | 50         |
|1\|U\|12\|9    | 1         | Unfollow     | 12           | 9          |
|542532\|B      | 542532    | Broadcast    | -            | -          |
|43\|P\|32\|56  | 43        | Private Msg  | 32           | 56         |
|634\|S\|32     | 634       | Status Update| 32           | -          |

Using the verification program supplied, you will receive exactly 10000000 events,
with sequence number from 1 to 10000000. **The events will arrive out of order**.

*Note: Please do not assume that your code would only handle a finite sequence
of events, **we expect your server to handle an arbitrarily large events stream**
(i.e. you would not be able to keep all events in memory or any other storage)*

Events may generate notifications for *user clients*. **If there is a
*user client* ** connected for them, these are the users to be
informed for different event types:

* **Follow**: Only the `To User Id` should be notified
* **Unfollow**: No clients should be notified
* **Broadcast**: All connected *user clients* should be notified
* **Private Message**: Only the `To User Id` should be notified
* **Status Update**: All current followers of the `From User ID` should be notified

If there are no *user client* connected for a user, any notifications
for them must be silently ignored. *user clients* expect to be notified of
events **in the correct order**, regardless of the order in which the
*event source* sent them.

### The Configuration

During development, it is possible to modify the test program behavior using the 
following environment variables:

1. **logLevel** - Default: info

   Modify to "debug" to print debug messages.

2. **eventListenerPort** - Default: 9090

   The port used by the event source.

3. **clientListenerPort** - Default: 9099

   The port used to register clients.

4. **totalEvents** - Default: 10000000

   Number of messages to send.

5. **concurrencyLevel** - Default: 100

   Number of conected users.

6. **numberOfUsers** Default: concurrencyLevel * 10
	
   Total number of users (connected or not)

7. **randomSeed** - Default: 666
	
   The seed to generate random values

8. **timeout** - Default: 20000
	
   Timeout in milliseconds for clients while waiting for new messages

9. **maxEventSourceBatchSize** - Default: 100

   The event source flushes messages in random batch sizes and ramdomize the messages
   order for each batch. For example, if this configuration is "1" the event source 
   will send only ordered messages flushing the connection for each message.

10. **logInterval** - Default: 1000

   The interval in milliseconds used to log the sent messages counter.

### Your Solution
We expect you to send us the source code of a fully functional server for the
proposed challenge **using the default configurations**. You still might want
to stress-test your code with different configuration parameters to make sure
it is not too tailored to our test-suite, and is generic enough.

The challenge was designed so that a candidate can implement the
solution using just the standard library of most programming languages
and environments (i.e. no external gems/JARs/libs/modules/etc.). Feel
free to use your preferred build and testing libraries, but the production
code should have very minimal dependencies on third-party code,
**preferably none at all**. E.g. we want to see what kinds of networking
abstractions you'll come up with for this challenge. Please don't take that
assessment criterion away from us by using an external library that does
that for you.

Your code should build and run on a Mac or GNU/Linux machine running a
recent OS release.

*As a **non-exhaustive** example, we have received successful applications
developed on: Node.js, Ruby, JRuby, Haskell, Clojure, Scala, Go, Python,
Java, and C/C++.*

If you absolutely think you need some sort of third-party library,
please write a paragraph to help us better understand your choice.

### Before submitting your code
With this document you  received a jar file and a shell script. These
contain one possible implementation of the *event source* and *user
client* described previously.

**We expect you to make sure that your solution works with the
supplied clients before sending it to us**. The first thing we will do
with your code is to run it agains these clients, so you can have very
early feedback by treating it as a test suite.

To run the clients, first make sure you have the server you wrote
running and listening to ports 9090 and 9099, then run:

```
$ ./followermaze.sh
```

This will start the clients, which will immediately start sending
message to your server. You know it finished without errors when it
outputs:

```
 [INFO] ==================================
 [INFO] \o/ ALL NOTIFICATIONS RECEIVED \o/
 [INFO] ==================================
```

### Assesment Criteria
We expect you to write **code you would consider production-ready**.
This means we want your code to be well-factored, without needless
duplication, follow good practices and be automatically verified.

What we will look at:
- If your code fulfils the requirement, and runs against the
supplied example server
- How clean is your design and implementation, how easy it is to
understand and maintain your code
- How you verified your software, if by automated tests or some
other way
- What kind of documentation you ship with your code







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