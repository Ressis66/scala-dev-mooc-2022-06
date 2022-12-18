import cats.data.{EitherT, OptionT, ReaderT}
import cats.effect.IO
import cats.effect.unsafe.implicits.global

implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

def getUsername: IO[Option[String]] = IO.pure(Some("Bob"))
def getId(name: String): IO[Option[Int]] = IO.pure(Some(42))
def getPermissions(id: Int): IO[Option[String]] = IO.pure(Some("permissions"))

//val result = for {
//  userName <- getUsername
//  id <- getId(userName)
//  permissions <- getPermissions(id)
//} yield (userName, id, permissions)

val result = for {
  userName <- OptionT(getUsername)
  id <- OptionT(getId(userName))
  permissions <- OptionT(getPermissions(id))
} yield (userName, id, permissions)

result.value.unsafeRunSync()

// lifting
val pure = OptionT.pure[IO]("pure value") // IO(Some("pure value"))
val fromOpt = OptionT.fromOption[IO](Some("pure value"))
val fromNone: OptionT[IO, Nothing] = OptionT.fromOption[IO](None)
val fromIO = OptionT.liftF(IO.pure("value"))
val fromIOError = OptionT.liftF(IO.raiseError(new Exception("Error")))

// EitherT

def action: IO[Int] = ???
sealed trait UserServiceError extends Product with Serializable
case class PermissionDenied(msg: String) extends UserServiceError
case class UserNotFound(userId: Int) extends UserServiceError

def getUserName(userId: Int): EitherT[IO, UserServiceError, String] =
  EitherT.pure("Jack")

def getUserAddress(userId: Int): EitherT[IO, UserServiceError, String] =
  EitherT.fromEither(Left(PermissionDenied("you're not a friend")))

def getProfile(id: Int) = for {
  name <- getUserName(id)
  address <- getUserAddress(id)
} yield (name, address)

getProfile(2).value.unsafeRunSync()

// ReaderT
trait ConnectionPool
case class Environment(cp: ConnectionPool)
def getUserAlias(id: Int): ReaderT[IO, Environment, String] =
  ReaderT(cp => IO.pure("Jack"))
def getComment(id: Int): ReaderT[IO, Environment, String] =
  ReaderT.fromFunction(cp => "comment text")
def updateComment(id: Int, text: String): ReaderT[IO, Environment, Unit] =
  ReaderT.liftF(IO.pure("comment text"))

val result = for {
  a <- getUserAlias(1)
  b <- getComment(2)
  _ <- updateComment(2, "New text")
} yield (a, b)

result(Environment(new ConnectionPool {})).unsafeRunSync()
