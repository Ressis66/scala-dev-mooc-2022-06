package module4.e26_http4s_03

import cats.data.{Kleisli, OptionT}
import cats.effect._
import cats.implicits._
import com.comcast.ip4s.{Host, Port}
import module4.e26_http4s_03.CirceJson.User
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRequest, AuthedRoutes, HttpRoutes}
import org.typelevel.ci.CIStringSyntax
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._

object Restful {

  type Sessions[F[_]] = Ref[F, List[String]]

  final case class AuthUser(name: String)

  val publicRoutes: HttpRoutes[IO] =
    HttpRoutes.of { case r @ POST -> Root / "echo" =>
      for {
        u <- r.as[User]
        _ <- IO.println(u)
        response <- Ok(u)
      } yield response
    }

  val helloService: AuthedRoutes[AuthUser, IO] =
    AuthedRoutes.of { case GET -> Root / "hello" as user =>
      Ok(s"Hello, ${user.name}")
    }

  def loginService(sessions: Sessions[IO]): HttpRoutes[IO] = HttpRoutes.of {
    case PUT -> Root / "register" / name =>
      sessions
        .update(list => name :: list)
        .flatMap(_ => Ok(s"Welcome, $name!"))
  }

  def routes(sessions: Sessions[IO]) =
    loginService(sessions) <+> sessionAuth(sessions)(helloService)

  def router(sessions: Sessions[IO]) =
    Router(
      "/" -> routes(sessions),
      "/public" -> publicRoutes
    )

  def sessionAuth(sessions: Sessions[IO]): AuthMiddleware[IO, AuthUser] =
    authedRoutes =>
      Kleisli { req =>
        req.headers.get(ci"X-User-Name") match {
          case Some(nel) =>
            val name = nel.head.value
            for {
              users <- OptionT.liftF(sessions.get)
              result <-
                if (users.contains(name))
                  authedRoutes(AuthedRequest(AuthUser(name), req))
                else OptionT.liftF(Forbidden("You shall not pass"))
            } yield result
          case None => OptionT.liftF(Forbidden("No header"))
        }
      }

  val server = for {
    sessions <- Resource.eval(Ref.of[IO, List[String]](List.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router(sessions).orNotFound)
      .build
  } yield s

  // curl localhost:8080/hello
  // curl -H "X-User-Name: vladimir" localhost:8080/hello
  // curl -XPUT localhost:8080/register/vladimir
  // curl -H "X-User-Name: vladimir" localhost:8080/hello
}
