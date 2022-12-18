package module4.e26_http4s_03

import cats.effect.{IOApp, IO, Resource}

import scala.concurrent.duration._

import cats.implicits._

object Main extends IOApp.Simple {
  def run: IO[Unit] = {
    for {
      resp <- Restful.server.use(_ => HttpClient.stringBodyResponse)
      _ <- IO.println(resp)
    } yield ()
  }
}
