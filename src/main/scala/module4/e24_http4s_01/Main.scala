package module4.e24_http4s_01

import cats.effect._

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    Restful.server.use(_ => IO.never)
}
