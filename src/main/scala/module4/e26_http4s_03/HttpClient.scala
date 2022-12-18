package module4.e26_http4s_03

import CirceJson._
import cats.effect.{IO, Ref}
import org.http4s.{EntityDecoder, Method, Request}
import org.http4s.circe._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import CirceEntityDecoder._
import CirceEntityEncoder._

object HttpClient {
  val builder = EmberClientBuilder.default[IO].build

  val request = Request[IO](uri = uri"http://localhost:8080/hello")

  val postRequest =
    Request[IO](
      method = Method.POST,
      uri = uri"http://localhost:8080/hello"
    ).withEntity(User("Vladimir", Some("vova@example.com")))

  val result = for {
    client <- builder
    response <- client.run(request)
  } yield response

  val response = result.use { res =>
    res.as[User].flatMap(IO.println)
  }

  val printResponse = result.use(IO.println)

  val stringBodyResponse =
    result.use(res => res.body.compile.to(Array).map(new String(_)))

  val pureRequest =
    Ref.of[IO, List[String]](List.empty).flatMap {
      (sessions: Restful.Sessions[IO]) =>
        val routes = Restful.routes(sessions)
        routes.run(request).value
    }

}
