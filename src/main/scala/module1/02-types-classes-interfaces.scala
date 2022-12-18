package module1

import sun.security.util.Password

import java.io.{Closeable, File}
import scala.io.{BufferedSource, Source}


object type_system {

  /**
   * Scala type system
   *
   */

  // AnyVal

  //


  // Unit


  // Null


  // Nothing

  def absurd(v: Nothing) = ???


  // Generics

  // работа с ресурсом

  lazy val file: File = ???

  lazy val source: BufferedSource = Source.fromFile(file)

  lazy val lines: Iterator[String] = source.getLines()

  //source.close()

//  lazy val lines2 = try {
//    source.getLines()
//  } finally {
//    source.close()
//  }

  def ensureClose[S <: Closeable, R](source: S)(f: S => R): R = {
    try {
      f(source)
    } finally {
      source.close()
    }
  }

  def foo(x: Int)(y: Int) = x + y

  def foo2(x: Int, y: Int) = x + y

  foo(2)(3) // 5

  foo2(2, 3) //5


  lazy val lines3: List[String] = ensureClose(source) { s =>
    s.getLines().toList
  }




    // ограничения связанные с дженериками


    /**
     *
     * class
     *
     * конструкторы / поля / методы / компаньоны
     */

    class User private(var email: String, var password: String = "") {
      def this(email: String) = this(email, "")
    }

    val user = User.from("foo@mail.com")

    object User {
      def from(email: String): User = new User(email = email)

      def from(email: String, password: String): User = new User(email, password)
    }

    case class User2(email: String, password: String)

    val user2 = User2("", "")

    val user3 = user2.copy(password = "fdb")


    /**
     * Задание 1: Создать класс "Прямоугольник"(Rectangle),
     * мы должны иметь возможность создавать прямоугольник с заданной
     * длиной(length) и шириной(width), а также вычислять его периметр и площадь
     *
     */

    class Rectangle(val width: Int, val height: Int) {
      def perimeter(): Int = width * 2 + height * 2

      def area(): Int = width * height
    }


    /**
     * object
     *
     * 1. Паттерн одиночка
     * 2. Линивая инициализация
     * 3. Могут быть компаньоны
     */


    /**
     * case class
     *
     */


    // создать case класс кредитная карта с двумя полями номер и cvc


    /**
     * case object
     *
     * Используются для создания перечислений или же в качестве сообщений для Акторов
     */


    /**
     * trait
     *
     */

    trait WitId{
      def typedId: String
    }

    trait UserService {

      def get(id: String): User

      def insert(user: User): Unit

      def foo: Int
    }

    class UserServiceImpl extends UserService with WitId {
      def get(id: String): User = ???

      def insert(user: User): Unit = ???

      val foo = 10

      override def typedId: String = ???
    }

  val userService = new UserService with WitId {
    override def get(id: String): User = ???

    override def insert(user: User): Unit = ???

    override def foo: Int = ???

    override def typedId: String = ???
  }



  class A {
    def foo() = "A"
  }

  trait B extends A {
    override def foo() = "B" + super.foo()
  }

  trait C extends B {
    override def foo() = "C" + super.foo()
  }

  trait D extends A {
    override def foo() = "D" + super.foo()
  }

  trait E extends C {
    override def foo(): String = "E" + super.foo()
  }

  // A -> D -> B -> C
  // CBDA
  val v = new A with D with C with B


  // A -> B -> C -> E -> D
  // DECBA
  val v1 = new A with E with D with C with B


  /**
   * Value classes и Universal traits
   */

}