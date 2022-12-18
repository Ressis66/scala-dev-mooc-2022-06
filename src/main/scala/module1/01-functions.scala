package module1

object functions {


  /**
   * Функции
   */

   def sum(x: Int, y: Int): Int = x + y

   sum(2, 3) // 5

   val sum2: (Int, Int) => Int = (a, b) => a + b

   sum2(2, 3) // 5

   val sum3 = sum _

   sum3(2, 3)



  /**
   * Реализовать ф-цию  sum, которая будет суммировать 2 целых числа и выдавать результат
   */



  // Currying

  val sumCurried: Int => Int => Int = sum2.curried

  val l: List[String] = List()

  val x6: Int => Int = sumCurried(2)
  val _: Int = x6(3) // 5

  
  // Partial function

  val divide: PartialFunction[(Int, Int), Int] = {
    case x if x._2 != 0 => x._1 / x._2
  }

  if(divide.isDefinedAt(10, 0))
    divide(10, 0)



  // SAM Single Abstract Method

  trait Printer{
    def apply(s: String): Unit
  }

  val p: Printer = a => println(a)

  p("hello")


  /**
   *  Задание 1. Написать ф-цию метод isEven, которая будет вычислять является ли число четным
   */


  /**
   * Задание 2. Написать ф-цию метод isOdd, которая будет вычислять является ли число нечетным
   */


  /**
   * Задание 3. Написать ф-цию метод filterEven, которая получает на вход массив чисел и возвращает массив тех из них,
   * которые являются четными
   */



  /**
   * Задание 4. Написать ф-цию метод filterOdd, которая получает на вход массив чисел и возвращает массив тех из них,
   * которые являются нечетными
   */


  /**
   * return statement
   *
   */
}
