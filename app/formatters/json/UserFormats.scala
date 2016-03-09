package formatters.json

import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserRoles}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object UserFormats {
  val restFormat = {
    implicit val enumFormat = UserRoles.rolesEnumFormat

    val userWrites = new Writes[User] {
      def writes(user: User) = Json.obj(
        "id" -> user.id,
        "loginInfo" -> user.loginInfo,
        "username" -> user.username,
        "firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "address" -> user.address,
        "phone" -> user.phone,
        "email" -> user.email,
        "position" -> user.position,
        "user_role" -> user.userRole
      )
    }

    val userReads: Reads[User] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "loginInfo").readNullable[LoginInfo] and
        (JsPath \ "username").readNullable[String] and
        (JsPath \ "firstName").readNullable[String] and
        (JsPath \ "lastName").readNullable[String] and
        (JsPath \ "address").readNullable[String] and
        (JsPath \ "phone").readNullable[String] and
        (JsPath \ "email").readNullable[String] and
        (JsPath \ "position").readNullable[String] and
        (JsPath \ "user_role").read[UserRoles.Value]
      )(User.apply _)

    Format(userReads, userWrites)
  }
}
