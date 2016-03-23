package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models._
import play.api.Logger
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
  val accessLogger: Logger = Logger("access")

  def getUser(id: Long) = SecuredAction.async {
    accessLogger.info(s"User $id requested")
    userService.getUserByID(id).map{
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "Not Found"))
    }
  }

  def getUserByEmail(email: String) = SecuredAction.async {
    accessLogger.info(s"User $email requested")
    userService.getUserByEmail(email).map{
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "Not Found"))
    }
  }

  def getAllUsers = SecuredAction.async {
    accessLogger.info("All users requested")
    userService.getAllUsers.map(users => Ok(Json.toJson(users)))
  }

  def createUserByManager = SecuredAction(ManagerRights).async(parse.json) { implicit request => {
    accessLogger.info("Manager created a user")
    request.body.validate[UserData].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      userData => {
        userService.createInactiveUser(userData).map(user => Ok(Json.toJson(user)))
      }
    )
  }}

  def editUser(id: Long) = SecuredAction.async(parse.json) { implicit request => {
    accessLogger.info(s"Edit user $id requested")
    request.body.validate[UserData].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      userData => {
        userService.editUser(id, userData).map(user => Ok(Json.toJson(user)))
      }
    )
  }}
}
