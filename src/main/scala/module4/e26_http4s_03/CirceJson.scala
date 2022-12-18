package module4.e26_http4s_03

import cats.effect.{IO, IOApp}
import cats.implicits._
import io.circe.literal._
import io.circe.syntax._
import io.circe.parser.parse
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.generic.semiauto._

object CirceJson extends IOApp.Simple {
  def run: IO[Unit] =
    IO.fromEither(parsedResult).flatMap(IO.println)

  // Encoder = A => Json
  // Decoder = Json => Either[Error, A]

  val example = json"""{"name": "Bob", "email": null}"""

  case class User(name: String, email: Option[String])

  case class Permission(user: User, id: Int)

//  implicit val decoderUser: Decoder[User] =
//    deriveDecoder[User]
  implicit val decoderPermission: Decoder[Permission] =
    deriveDecoder[Permission]

  implicit val decoderUser: Decoder[User] =
    Decoder.instance { cur =>
      for {
        name <- cur.downField("name").as[String]
        email <- cur.downField("email").as[Option[String]]
      } yield User(name, email)
    }

  implicit val encoderUser: Encoder[User] =
    deriveEncoder

  val parsedResult =
    parse("""{"name": "Bob", "email": null}""").flatMap(_.as[User])

  val decodingResult = example.as[User]

}
