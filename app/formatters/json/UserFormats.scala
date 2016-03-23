package formatters.json

import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserRoles}
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._

object UserFormats {
  val restFormat = {
    implicit val enumFormat = UserRoles.rolesEnumFormat

    val onlyEmail: Reads[String] =
      Reads.StringReads.filter(ValidationError("Invalid email given!"))(str => {
        str.matches("""^([a-z0-9_-]+\.)*[a-z0-9_-]+@[a-z0-9_-]+(\.[a-z0-9_-]+)*\.[a-z]{2,6}$""")
      })

    val onlyPhone: Reads[String] =
      Reads.StringReads.filter(ValidationError("Invalid phone number given!"))(str => {
        str.matches("""^(\+?(\d{1}|\d{2}|\d{3})[- ]?)?\d{3}[- ]?\d{2}[- ]?\d{2}$""")
      })

    val onlyUserRole: Reads[UserRoles.Value] =
      enumFormat.filter(ValidationError("Invalid user role given!"))(role => {
        role == UserRoles.User || role == UserRoles.Manager
      })

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
        "userRole" -> user.userRole
      )
    }

    val userReads: Reads[User] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "loginInfo").readNullable[LoginInfo] and
        (JsPath \ "username").readNullable[String] and
        (JsPath \ "firstName").readNullable[String] and
        (JsPath \ "lastName").readNullable[String] and
        (JsPath \ "address").readNullable[String] and
        (JsPath \ "phone").readNullable[String](onlyPhone) and
        (JsPath \ "email").readNullable[String](onlyEmail) and
        (JsPath \ "position").readNullable[String] and
        (JsPath \ "userRole").read[UserRoles.Value](onlyUserRole)
      )(User.apply _)

    Format(userReads, userWrites)
  }
}
