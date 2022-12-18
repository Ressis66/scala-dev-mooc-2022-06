package module4.e25_http4s_02

import cats.Functor
import cats.data.Kleisli
import cats.effect._
import com.comcast.ip4s.{Host, Port}
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Status}
import org.typelevel.ci.CIStringSyntax

object SessionsNoMiddleware {

  type Sessions[F[_]] = Ref[F, List[String]]

  def service(sessions: Sessions[IO]): HttpRoutes[IO] =
    HttpRoutes.of {
      case r @ GET -> Root / "hello" =>
        r.headers.get(ci"X-User-Name") match {
          case Some(nel) =>
            val name = nel.head.value
            sessions.get.flatMap(users =>
              if (users.contains(name)) Ok(s"Hello, $name")
              else Forbidden("You shall not pass")
            )
          case None => Forbidden("No header")
        }

      case PUT -> Root / "register" / name =>
        sessions
          .update(list => name :: list)
          .flatMap(_ => Ok(s"Welcome, $name!"))
    }

  def router(sessions: Sessions[IO]) =
    AddResponseHeader.addResponseHeaderMiddleware(
      Router(
        "/" -> service(sessions)
      )
    )

  val server = for {
    sessions <- Resource.eval(Ref.of[IO, List[String]](List.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router(sessions).orNotFound)
      .build
  } yield s

  // curl localhost:8080/hello/vladimir
  // curl localhost:8080/hello
  // curl -H "X-User-Name: vladimir" localhost:8080/hello
  // curl -XPUT localhost:8080/register/vladimir
  // curl -H "X-User-Name: vladimir" localhost:8080/hello
}
