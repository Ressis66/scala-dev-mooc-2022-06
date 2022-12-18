package module4.e25_http4s_02

import cats.effect._

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    SessionsAuthMiddleware.server.use(_ => IO.never)
}
