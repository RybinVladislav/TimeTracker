package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.Logger
import services.users.UsersService

import scala.concurrent.Future

class Application @Inject() (val messagesApi: MessagesApi,
                             val env: Environment[User, JWTAuthenticator],
                             userService: UsersService) extends Silhouette[User, JWTAuthenticator] {

  val accessLogger: Logger = Logger("access")
  implicit val userFormat = formatters.json.UserFormats.restFormat
  implicit val entryFormat = formatters.json.TimeEntryFormats.restFormat

  def index = UserAwareAction.async { implicit request => {
    accessLogger.info("Index requested")
    Future.successful(Ok(Json.toJson(request.identity.toString)))
  }}
}