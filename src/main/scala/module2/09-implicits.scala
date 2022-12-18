package module2

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object implicits {

  // implicit conversions

  object implicit_conversions {

    /** Расширить возможности типа String, методом trimToOption, который возвращает Option[String]
      * если строка пустая или null, то None
      * если нет, то Some от строки со всеми удаленными начальными и конечными пробелами
      */

    lazy  val str: String = "we"

    class StringOps(str: String){
      def trimToOption: Option[String] =
        Option(str).map(_.trim).filter(_.nonEmpty)
    }

    implicit def strToStringOps(string: String): StringOps = new StringOps(string)

    str.trimToOption

    // implicit conversions ОПАСНЫ

    implicit def strToInt(string: String): Int = Integer.parseInt(string)

   // "foo" / 42

    implicit val seq = Seq("a", "b", "c") // Int -> Char


    def log(str: String) = println(str)

    val res = log(2)

  }

  object implicit_scopes {

    trait Printable

    trait Printer[T] extends Printable {
      def print(v: T): Unit
    }

    object Printable {
//       implicit val v: Printer[Bar] = new Printer[Bar] {
//         override def print(v: Bar): Unit = println(s"Implicit from companion object Printable + $v")
//       }
    }

    // companion object Printer
    object Printer {
       implicit val v: Printer[Bar] = new Printer[Bar] {
         override def print(v: Bar): Unit = println(s"Implicit from companion object Printer + $v")
       }
    }

    case class Bar()

    // companion object Bar
    object Bar {
//        implicit val v: Printer[Bar] = new Printer[Bar] {
//          override def print(v: Bar): Unit = println(s"Implcit from companion object Bar + $v")
//        }
    }

    // some arbitrary object
    object wildcardImplicits {
      implicit val v: Printer[Bar] = new Printer[Bar] {
        override def print(v: Bar): Unit = println(s"Implcit from wildcard import + $v")
      }
    }

    def foo[T: Printer](b: Bar)(implicit m: Printer[Bar]) = m.print(b)

//     implicit val v1 = new Printer[Bar]{
//       def print(v: Bar): Unit = println(s"Implcit from local val + $v")
//     }

 //   import wildcardImplicits._

    val result = foo(Bar())

    // 1. Local scope | package object 
    // 2. wildcard import

  }


}
