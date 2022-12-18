package module2

import module2.type_classes.JsValue.{JsNull, JsNumber, JsString}


object type_classes {

  sealed trait JsValue
  object JsValue {
    final case class JsObject(get: Map[String, JsValue]) extends JsValue
    final case class JsString(get: String) extends JsValue
    final case class JsNumber(get: Double) extends JsValue
    final case object JsNull extends JsValue
  }

  trait JsonWriter[T]{
    def write(v: T): JsValue
  }

  object JsonWriter{
    def apply[T](implicit ev: JsonWriter[T]): JsonWriter[T] = ev

    def from[T](f: T => JsValue) = new JsonWriter[T] {
      override def write(v: T): JsValue = f(v)
    }

    implicit val strToJsValue = from[String](JsString)

    implicit val intToJsValue = from[Int](JsNumber(_))

    implicit def optToJsValue[A](implicit ev: JsonWriter[A]): JsonWriter[Option[A]] =
      from[Option[A]] {
        case Some(value) => ev.write(value)
        case None => JsNull
      }
  }

  implicit class JsonSyntax[T](v: T){
    def toJson(implicit ev: JsonWriter[T]): JsValue = ev.write(v)
  }

  def toJson[T: JsonWriter](v: T): JsValue = {
    JsonWriter[T].write(v)
  }

  toJson("vfvfvfv")
  toJson(12)
  toJson(Option(12))
  toJson(Option("dfddssdfds"))

  "dvfvffvf".toJson
   Option(12).toJson





  // 1 компонент type constructor
  trait Ordering[T]{

    def less(a: T, b: T): Boolean
  }

  object Ordering{

    def from[A](f: (A, A) => Boolean): Ordering[A] = new Ordering[A] {
      override def less(a: A, b: A): Boolean = f(a, b)
    }

    // 2 имплисит занчения / функции (инстансы тайп класса)

    implicit val intOrdering = Ordering.from[Int]((a, b) => a < b)

    implicit val strOrdering = Ordering.from[String]((a, b) => a < b)
  }

  // 3 имплисит параметр

  def _max[A](a: A, b: A)(implicit ordering: Ordering[A]): A =
    if(ordering.less(a, b)) b else a

  _max(5, 10)
  _max("ab", "abcd")




  // 1
  trait Eq[T]{
    def ===(a: T, b: T): Boolean
  }

  object Eq{
    def apply[T](): Eq[T] = ???

    // 2
    implicit val eqStr: Eq[String] = new Eq[String]{
      override def ===(a: String, b: String): Boolean = a == b
    }
  }

  // 4
  implicit class EqSyntax[T](a: T){

    // 3
    def ===(b: T)(implicit eq: Eq[T]): Boolean =
      eq.===(a, b)
  }

  val result = List("a", "b", "c").filter(str => str === "a")


//  def tuplef[F[_]: Bindable, A, B](fa: F[A], fb: F[B]): F[(A, B)] = {
//    Bindable[F](fa).flatMap(v1 =>
//      Bindable[F](fb).map(v2 => (v1, v2))
//    )
//  }
//
//  // 1
//  trait Bindable[F[_]] {
//    def map[A, B](fa: F[A])(f: A => B): F[B]
//    def flatMap[A, B](fb: F[A])(f: A => F[B]): F[B]
//  }
//
//  object Bindable{
//    def apply[F[_], A](implicit ev: Bindable[F[A]]): Bindable[F[A]] = ev
//  }

}
