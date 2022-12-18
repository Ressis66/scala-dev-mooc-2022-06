package module4.e24_http4s_01

import cats.effect._
import com.comcast.ip4s.{Host, Port}
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object Restful {

  val serviceOne: HttpRoutes[IO] =
    HttpRoutes.of { case GET -> Root / "hello" / name =>
      Ok(s"hello, $name")
    }

  val serviceTwo: HttpRoutes[IO] =
    HttpRoutes.of { case GET -> Root / "wazzup" / name =>
      Ok(s"wazzup, $name")
    }

  val router = Router("/" -> serviceOne, "/api" -> serviceTwo)

  val server = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router.orNotFound)
      .build
  } yield s

}
