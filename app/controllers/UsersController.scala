package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import security.{ManagerRights, UserData}
import services.users.UsersService

import scala.concurrent.Future

class UsersController @Inject() (val messagesApi: MessagesApi,
                                  val env: Environment[User, JWTAuthenticator],
                                  userService: UsersService) extends Silhouette[User, JWTAuthenticator] {

  implicit val userFormat = formatters.json.UserFormats.restFormat
  implicit val entryFormat = formatters.json.TimeEntryFormats.restFormat

  def getUser(id: Long) = SecuredAction.async {
    userService.getUserByID(id).map{
      case Some(user) => Ok(Json.toJson(user))
      case None => Ok(Json.toJson("null"))
    }
  }

  def getUserByEmail(email: String) = SecuredAction.async {
    userService.getUserByEmail(email).map{
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.toJson("null"))
    }
  }

  def getAllUsers = SecuredAction.async {
    userService.getAllUsers.map(users => Ok(Json.toJson(users)))
  }

  def createUserByManager = SecuredAction(ManagerRights).async(parse.json) { implicit request =>
    request.body.validate[UserData].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      userdata => {
        val user = User(0, None, Some(userdata.username),
          Some(userdata.firstName), Some(userdata.lastName), Some(userdata.address),
          Some(userdata.phone), Some(userdata.email), Some(userdata.position), UserRoles.User)
        userService.createInactiveUser(user).map(user => Ok(Json.toJson(user)))
      }
    )
  }

  def editUser(id: Long) = SecuredAction.async(parse.json) { implicit request =>
    request.body.validate[UserData].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      userdata => {
        val user = User(0, None, None,
          Some(userdata.firstName), Some(userdata.lastName), Some(userdata.address),
          Some(userdata.phone), None, Some(userdata.position), UserRoles.User)
        userService.editUser(id, user).map(user => Ok(Json.toJson(user)))
      }
    )
  }
}
