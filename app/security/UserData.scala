package security

import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserRoles}
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._


case class UserData(username: String,
                    firstName: String,
                    lastName: String,
                    address: String,
                    phone: String,
                    email: String,
                    position: String)

object UserData {
  implicit val userWrites = new Writes[UserData] {
    def writes(user: UserData) = Json.obj(
      "username" -> user.username,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "address" -> user.address,
      "phone" -> user.phone,
      "email" -> user.email,
      "position" -> user.position
    )
  }

  implicit val userReads: Reads[UserData] = (
    (JsPath \ "username").read[String] and
      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "address").read[String] and
      (JsPath \ "phone").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "position").read[String]
    )(UserData.apply _)
}
