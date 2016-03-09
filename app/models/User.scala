package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json._

/**
  * The user object.
  *
  * @param id The unique ID of the user.
  * @param loginInfo The linked login info.
  * @param username Maybe the full name of the authenticated user.
  * @param firstName Maybe the first name of the authenticated user.
  * @param lastName Maybe the last name of the authenticated user.
  * @param address Maybe the address of the authenticated provider.
  * @param phone Maybe the phone of the authenticated provider.
  * @param email Maybe the email of the authenticated provider.
  * @param position Maybe the position of the authenticated provider.
  *
  */
case class User(id: Long,
                loginInfo: Option[LoginInfo],
                username: Option[String],
                firstName: Option[String],
                lastName: Option[String],
                address:Option[String],
                phone: Option[String],
                email: Option[String],
                position: Option[String],
                userRole: UserRoles.Value) extends Identity

object UserRoles extends Enumeration {
  type UserRoles = Value
  val Manager, User = Value

  implicit val rolesEnumFormat = new Format[UserRoles.Value] {
    def reads(json: JsValue) = JsSuccess(UserRoles.withName(json.as[String]))
    def writes(role: UserRoles.Value) = JsString(role.toString)
  }
}


