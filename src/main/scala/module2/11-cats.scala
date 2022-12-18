package module2

import cats.{Id, Monad}
import cats.data.{Chain, Ior, Kleisli, NonEmptyChain, NonEmptyList, OptionT, Validated, ValidatedNec, Writer, WriterT}
import cats.implicits._

import scala.concurrent.Future
import scala.util.Try

object dataStructures{

  /**
   * Chain
   */

  // Конструкторы

  val empty: Chain[Int] = Chain[Int]()
  val empty2: Chain[Int] = Chain.empty[Int]

  val ch2 = Chain(1)
  val ch3 = Chain.one(1)
  val ch4: Chain[Int] = Chain.fromSeq(List(1, 2, 3))


  // операторы

  val ch5 = ch2 :+ 2
  val ch6 = 3 +: ch2
  val r = ch2.headOption

  ch3.map(_ + 1)
  ch3.flatMap(v =>  Chain.one(v + 1))

  /**
   * NonEmptyChain
   */

  // конструкторы

  val nec: NonEmptyChain[Int] = NonEmptyChain(1)
  val nec2: NonEmptyChain[Int] = NonEmptyChain.one(1)
  val nec3: Option[NonEmptyChain[Int]] =
    NonEmptyChain.fromSeq(List(1, 2, 3))

  val r2 = nec.head

  /**
   * NonEmptyList
   **/

  val nel1: NonEmptyList[Int] = NonEmptyList(1, List(2, 3))
  val nel2: NonEmptyList[Int] = NonEmptyList.one(1)
  val nel3: Option[NonEmptyList[Int]] = NonEmptyList.fromList(List(1, 2, 3))

  val r3 = nec.head

}

object validation{

  type EmailValidationError = String
  type NameValidationError = String
  type AgeValidationError = String
  type Name = String
  type Email = String
  type Age = Int

  case class UserDTO(email: String, name: String, age: Int)
  case class User(email: String, name: String, age: Int)

  def emailValidatorE: Either[EmailValidationError, Email] = Left("Not valid email")

  def userNameValidatorE: Either[NameValidationError, Name] =  Left("Not valid name")

  def userAgeValidatorE: Either[AgeValidationError, Age] = Right(30)


  // короткое замыкание
  def validateUserDataE(userDTO: UserDTO): Either[String, User] = for{
    email <- emailValidatorE
    name <- userNameValidatorE
    age <- userAgeValidatorE
  } yield User(email, name, age)



  // Validated

  val v1: Validated[String, String] = Validated.valid("foo")
  val v2: Validated[String, String] = Validated.invalid("foo")


  //Конструкторы


  def emailValidatorV: Validated[EmailValidationError, Email] =
    "Email not valid".invalid[String]
  def userNameValidatorV: Validated[NameValidationError, Name] =
    "User name not valid".invalid[String]
  def userAgeValidatorV: Validated[String, Age] =
    30.valid[String]

  // Операторы

//  v1.map()
//  v1.bimap(e => , v=> )
//  v1 combine v2

  // Решаем задачу валидации с помощью Validated


  // не компилируется
  // НЕ МОНАДА
//  def validateUserDataV(userDTO: UserDTO): Validated[String, User] = for{
//    email <- emailValidatorV
//    name <- userNameValidatorV
//    age <- userAgeValidatorV
//  } yield User(email, name, age)

  def validateUserDataV2(userDTO: UserDTO): Validated[String, String] =
    emailValidatorV combine userNameValidatorV combine
      userAgeValidatorV.map(_.toString)

  def validateUserDataV3(userDTO: UserDTO): ValidatedNec[String, User] = (
    emailValidatorV.toValidatedNec,
    userNameValidatorV.toValidatedNec,
    userAgeValidatorV.toValidatedNec
  ).mapN{ (email, name, age) =>
    User(email, name, age)
  }

  def validateUserDataV4(userDTO: UserDTO) = ???


   // Ior

  val u: User = User("a", "b", 30)

  // Конструкторы

lazy  val ior: Ior[String, User] = Ior.Left("Error")
lazy  val ior2: Ior[String, User] = Ior.Right(u)
lazy  val ior3: Ior[String, User] = Ior.Both("Warning", u)

  // Операторы


  // Решаем задачу

  def emailValidatorI = Ior.both("Email ???", "f@f.com")
  def userNameValidatorI = Ior.both("Name ???", "John")
  def userAgeValidatorI = 30.rightIor[String]


  // В отличии от Validated является монадой
  def validateUserDataI(userDTO: UserDTO): Ior[NonEmptyChain[String], User] = for{
    email <- emailValidatorI.toIorNec
    name <- userNameValidatorI.toIorNec
    age <- userAgeValidatorI.toIorNec
  } yield User(email, name, age)

  lazy val validateUserDataI2 = ???
}

object functional {


  // Kleisli

  val f1: Int => String = i => i.toString
  val f2: String => String = s => s + "_oops"
  val f3: Int => String = f1 andThen f2

  val f4: String => Option[Int] = _.toIntOption

  val f5: Int => Option[Int] = i => Try(10 / i).toOption

  val f6: Kleisli[Option, String, Int] = Kleisli(f4) andThen Kleisli(f5)

  val _f6: String => Option[Int] = f6.run

  _f6("2") // 5

}

object transformers {

  val f1: Future[Int] = Future.successful(2)
  def f2(i: Int): Future[Option[Int]] = Future.successful(Try(10 / i).toOption)
  def f3(i: Int): Future[Option[Int]] = Future.successful(Try(10 / i).toOption)

  import scala.concurrent.ExecutionContext.Implicits.global

  val r: OptionT[Future, Int] = for{
    i1 <- OptionT.liftF(f1)
    i2 <- OptionT(f2(i1))
    i3 <- OptionT(f3(i2))
  } yield i2 + i3

  val _: Future[Option[Int]] = r.value

}


object cats_type_classes{

  // Semigroup

  trait Semigroup[A]{
    // ассоциативная бинарная операция
    // combine(x, combine(y, z)) == combine(combine(x, y), z)
    def combine(x: A, y: A): A
  }

  object Semigroup{
    def apply[A](implicit ev: Semigroup[A]) = ev

    implicit val intSemigroup = new Semigroup[Int] {
      override def combine(x: Int, y: Int): Int = x + y
    }
  }

  val _ = List(1, 2, 3).foldLeft(0)(_ |+| _)

  // Map("a" -> 1, "b" -> 2)
  // Map("b" -> 3, "c" -> 4)
  // Map("a" -> 1, "b" -> 5, "c" -> 4)

  def optCombine[V: Semigroup](v: V, optV: Option[V]): V =
    optV.map(Semigroup[V].combine(v, _)).getOrElse(v)

  def mergeMap[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
    lhs.foldLeft(rhs){ case (acc, (k, v)) =>
      acc.updated(k, optCombine(v, acc.get(k)))
    }

  def combineAll[A: Monoid](l: List[A]): A =
    l.foldLeft(Monoid[A].empty)(Monoid[A].combine(_, _))


  // Monoid

  trait Monoid[A] extends Semigroup[A]{
    def empty: A
    // associativity
    // combine(x, combine(y, z)) == combine(combine(x, y), z)
    // identity
    // combine(x, empty) == x
    // combine(empty, x) == x
  }

  object Monoid{
    def apply[A](implicit ev: Monoid[A]) = ev
  }


  // Functor

  trait Functor[F[_]]{
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  def id[A](e: A): A = e
  // map id map(id) == a

  Monad

  // f и g
  // map function composition f andThen g => map(f andThen g) == map(f) andThen map(g)

}