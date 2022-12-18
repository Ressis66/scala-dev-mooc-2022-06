package module1

object pattern_matching{
    // Pattern matching



       class Person(val name: String, val age: Int)

       object Person {
         def apply(n: String, a: Int): Person = ???
         def unapply(p: Person): Option[(String, Int)] = ???
       }

       lazy val tonyStark = Person("Tony", 43)

       val Person(n, b) = tonyStark

       println(s"$n $b")


      /**
       * используя паттерн матчинг напечатать имя и возраст
       */


       def printNameAndAge: Unit = tonyStark match {
         case Person(n, a) =>  println(s"$n $a")
       }


      final case class Employee(name: String, address: Address)
      final case class Address(street: String, number: Int)

      val alex = Employee("Alex", Address("XXX", 221))

      /**
       * воспользовавшись паттерн матчингом напечатать номер из поля адрес
       */


      alex match {
        case Employee(_, Address(_, n)) => println(n)
      }

      /**
       * Паттерн матчинг может содержать литералы.
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно соотвествовать Alex
       * 2. Все остальные
       */


       alex match {
         case Employee("Alex", _) =>
         case _ =>
       }


      /**
       * Паттерны могут содержать условия. В этом случае case сработает,
       * если и паттерн совпал и условие true.
       * Условия в паттерн матчинге называются гардами.
       */




      /**
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно начинаться с A
       * 2. Все остальные
       */

       alex match {
         case Employee(name, _) if name.startsWith("A") => //
         case _ => //
       }



      /**
       *
       * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
       * x @ ..., где x это любая переменная.
       * Это переменная может использоваться, как в условии,
       * так и внутри кейса
       */

        trait PaymentMethod
        case object Card extends PaymentMethod
        case object WireTransfer extends PaymentMethod
        case object Cash extends PaymentMethod

        case class Order(paymentMethod: PaymentMethod)

        lazy val order: Order = ???

        lazy val pm: PaymentMethod = ???


        def checkByCard(o: Order) = ???

        def checkOther(o: Order) = ???


        order match {
          case o @ Order(Card) => checkByCard(o)
          case o => checkOther(o)
        }


      /**
       * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
       */

       sealed trait A
       case class B(v: Int) extends A
       case class C(v: Int) extends A
       case class D(v: Int) extends A

      val a: A = ???

      a match {
        case B(_) | C(_) =>
        case D(v) =>
      }

}