package module1

import scala.util.Random

class BallsExperiment {
 val list = List(0,0,0,1,1,1)

 def isFirstBlackSecondWhite(): Boolean ={
  val list2 = Random.shuffle(list).take(2)
  if (list2.apply(0)==0 && list2.apply(1)==1) true
  else false
 }

}

object BallsTest {
 def main (args: Array[String]): Unit ={
  val count = 10000
  val listOfExperiments: List[BallsExperiment] = {
   for(i <- 1 to count) yield listOfExperiments.::(new BallsExperiment)
   listOfExperiments
   }


  val countOfExperiments = {
   listOfExperiments.map(x=>x.isFirstBlackSecondWhite())
  }
  val countOfPositiveExperiments: Float = countOfExperiments.count(_ == true)
  println(countOfPositiveExperiments / count)



 }
}