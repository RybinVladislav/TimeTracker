package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import services.users.UsersService

import scala.concurrent.Future

class UsersController @Inject() (val messagesApi: MessagesApi,
                                  val env: Environment[User, JWTAuthenticator],
                                  userService: UsersService) extends Silhouette[User, JWTAuthenticator] {

  implicit val userFormat = formatters.json.UserFormats.restFormat
  implicit val entryFormat = formatters.json.TimeEntryFormats.restFormat

  def getUser(id: Long) = Action.async {
    userService.getUserByID(id).map{
      case Some(user) => Ok(Json.toJson(user))
      case None => Ok(Json.toJson("null"))
    }
  }

  def getAllUsers = Action.async {
    userService.getAllUsers.map(users => Ok(Json.toJson(users)))
  }

  def createInactiveUser = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      user => {
        userService.createUser(user).map(user => Ok(Json.toJson(user)))
      }
    )
  }
  def editUser(id: Long) = ???
}
