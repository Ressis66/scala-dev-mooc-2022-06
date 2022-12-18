package module4

import cats.effect.kernel.Resource
import cats.effect.std.Queue
import cats.effect.{IO, IOApp}
import fs2.{Pipe, Stream}

import scala.concurrent.duration._

object Streams extends IOApp.Simple {

  val ios = IO.println("Hello") *>
    IO.println("Hello") *>
    IO.println("Hello") *>
    IO.println("Hello")

  val pureApply = Stream.apply(1, 2, 3).toList
  val ioApply = Stream.apply(1, 2, 3).covary[IO].compile.toList

  def foo(s: Stream[IO, Int]) = ???

  val unfolded = Stream.unfold(0) { s =>
    val next = s + 10
    if (s >= 50) None
    else Some((next.toString, next))
  }

  val repeated = unfolded.repeat.take(10)

  val echo = Stream.eval(IO.readLine).repeat.take(3).evalMap(IO.println)

  type Descriptor = String
  def openFile: IO[Descriptor] =
    IO.println("Opening File").as("File Descriptor")
  def closeFile(descriptor: Descriptor): IO[Unit] =
    IO.println(s"Closing $descriptor")
  def readFile(descriptor: Descriptor): Stream[IO, Byte] =
    Stream.emits(s"$descriptor content".map(_.toByte).toArray)

  val resource = Resource.make(openFile)(closeFile)
  val resourceStream = Stream
    .resource(resource)
    .flatMap(readFile)
    .map(b => b.toInt + 100)

  val withFixedDelay =
    Stream.fixedDelay[IO](1.second).evalMap(_ => IO.println("Hello, world!"))
  val withFixedRate =
    Stream.fixedRate[IO](1.second).evalMap(_ => IO.println("Hello, world!"))

  val queueIO = Queue.bounded[IO, Int](100)
  def putInQueue(queue: Queue[IO, Int], value: Int) = queue.offer(value)
  import cats.implicits._

  val fromQueue = Stream.force(for {
    q <- queueIO
    _ <- (IO.sleep(500.millis) *> putInQueue(q, 5)).replicateA(10).start
  } yield Stream.fromQueueUnterminated(q))

  val plus10ToStringPipe: Pipe[IO, Int, String] = s =>
    s.map(i => (i + 10).toString)

  def run: IO[Unit] =
    fromQueue.evalMap(IO.println).compile.drain
}
