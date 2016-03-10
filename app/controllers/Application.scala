package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{EntryStatus, TimeEntry, User, UserRoles}
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.users.UsersService

import scala.concurrent.Future

class Application @Inject() (val messagesApi: MessagesApi,
                             val env: Environment[User, JWTAuthenticator],
                             userService: UsersService) extends Silhouette[User, JWTAuthenticator] {

  implicit val userFormat = formatters.json.UserFormats.restFormat
  implicit val entryFormat = formatters.json.TimeEntryFormats.restFormat

  def index = UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.index(request.identity.toString)))
  }
}