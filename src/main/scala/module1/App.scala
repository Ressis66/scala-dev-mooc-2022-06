package module1

import module2.implicits.{implicit_conversions, implicit_scopes}
import module2.validation
import module2.validation.UserDTO
import module3.functional_effects
import module3.functional_effects.functionalProgram.declarativeEncoding.Console

import scala.util.{Failure, Success, Try}

object App {
  def main(args: Array[String]): Unit = {
    println(s"Hello from " +
      s"${Thread.currentThread().getName}")

//    val t1 = new Thread{
//      override def run(): Unit = {
//        Thread.sleep(1000)
//        println(s"Hello from " +
//          s"${Thread.currentThread().getName}")
//      }
//    }
//    val t2 = new threads.Thread1
//    t1.start()
//    t1.join()
//    t2.start()



//    def rates = {
//      val t1 = threads.getRatesLocation5
//      val t2 = threads.getRatesLocation6
//      t1.onComplete{ i1 =>
//        t2.onComplete{ i2 =>
//          println(s"Result: ${i1 + i2}")
//        }
//      }
//
//      val r: threads.ToyFuture[Int] = for{
//        i1 <- t1
//        i2 <- t2
//      } yield  i1 + i2
//
//      r.onComplete(println(_))
//    }
//
//    threads.printRunningTime(rates)

//    tryObj.readFromFile2() match {
//      case Failure(exception) => println(exception.getMessage)
//      case Success(value) => println(value)
//    }
    import scala.concurrent.ExecutionContext.Implicits.global

//    def rates = {
//      val f1 = future.getRatesLocation1
//      val f2 = future.getRatesLocation2
//      for{
//        v1 <- f1
//        v2 <- f2
//      } yield println(v1 + v2)
//    }
//
//    future.printRunningTime(future.getRatesLocation1 zip future.getRatesLocation2)

   // future.f3

//    println(promise.p1.isCompleted)
//    println(promise.f1.isCompleted)
//    promise.p1.complete(Try(10))
//    println(promise.p1.isCompleted)
//    println(promise.f1.isCompleted)
//    promise.f1.foreach(println)

//    implicit_scopes.result

//    Thread.sleep(4000)

//    println(validation.validateUserDataV2(UserDTO("", "", 30)))
//    println(validation.validateUserDataI(UserDTO("", "", 30)))

     // val r: () => Unit = functional_effects.functionalProgram.executableEncoding.greet2.run


    functional_effects.functionalProgram.declarativeEncoding.interpret(
       functional_effects.functionalProgram.declarativeEncoding.p2)

  }
}
