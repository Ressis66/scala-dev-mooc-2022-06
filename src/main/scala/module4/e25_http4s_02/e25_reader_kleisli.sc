import cats.data.{Kleisli, Reader}
import cats.effect.IO
import cats.effect.unsafe.implicits.global

implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
case class Config(fileName: String, poolConnections: Int)

val getDbName: Reader[Config, String] = Reader { (t: Config) =>
  t.fileName ++ ".db"
}
val multiplyConnections: Reader[Config, Int] = Reader { (t: Config) =>
  t.poolConnections * 8
}

val result: Reader[Config, String] = for {
  dbName <- getDbName
  realConnections <- multiplyConnections
} yield s"$dbName:$realConnections"

result.run(Config("file.json", 10))

type RIO[Env, A] = Kleisli[IO, Env, A]

val printConfig: Kleisli[IO, Config, Unit] =
  Kleisli { (c: Config) => IO(println(s"Reading ${c.fileName}")) }
val wrapConnections: Kleisli[IO, Config, Int] =
  Kleisli { (c: Config) => IO.pure(c.poolConnections) }

val rIO = for {
  _ <- printConfig
  result <- wrapConnections
} yield result

rIO.run(Config("file.json", 10)).unsafeRunSync()

val first: Kleisli[IO, Config, String] =
  Kleisli { (c: Config) => IO.pure(c.fileName) }
val second: Kleisli[IO, String, Unit] =
  Kleisli { (s: String) => IO.println(s) }
val third: Kleisli[IO, Unit, Unit] =
  Kleisli { (_: Unit) => IO.println("Launched missles") }

val aa = first.andThen(second).andThen(third)
