package module4.e25_http4s_02

import cats.data.{Kleisli, OptionT}
import cats.effect._
import cats.{Functor, Monad}
import com.comcast.ip4s.{Host, Port}
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, Header, HttpRoutes, Request, Status}
import org.typelevel.ci.CIString

object AddResponseHeader {

  val serviceOne: HttpRoutes[IO] =
    HttpRoutes.of { case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name")
    }

  val serviceTwo: HttpRoutes[IO] =
    HttpRoutes.of { case GET -> Root / "wazzup" / name =>
      Ok(s"wazzup, $name")
    }

  val router = addResponseHeaderMiddleware(
    Router("/" -> serviceOne, "/api" -> serviceTwo)
  )

  def addResponseHeaderMiddleware[F[_]: Functor](
      route: HttpRoutes[F]
  ): HttpRoutes[F] =
    Kleisli { req =>
      val result = route(req)
      result.map {
        case Status.Successful(res) => res.putHeaders("X-Otus" -> "Hello!")
        case res                    => res
      }
    }

  val server = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router.orNotFound)
      .build
  } yield s

  // curl localhost:8080/hello/vladimir --verbose
}
