package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{TimeEntry, User, UserRoles}
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Action
import security.UserRights
import services.entries.TimeEntriesService

import scala.concurrent.Future

class TimeEntriesController @Inject() (val messagesApi: MessagesApi,
                                       val env: Environment[User, JWTAuthenticator],
                                       timeEntriesService: TimeEntriesService) extends Silhouette[User, JWTAuthenticator] {

  implicit val format = formatters.json.TimeEntryFormats.restFormat

  def createEntry() = SecuredAction(UserRights).async(parse.json) { implicit request =>
    request.body.validate[TimeEntry].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      timeEntry => {
        timeEntriesService.createEntry(timeEntry).map(entry => Ok(Json.toJson(entry)))
      }
    )
  }

  def getEntry(id: Long) = SecuredAction.async {
    timeEntriesService.getEntryByID(id).map{
      case Some(entry) => Ok(Json.toJson(entry))
      case None => BadRequest(Json.obj("message" -> "Couldn't find entry."))
    }
  }

  def editEntry(id: Long) = SecuredAction.async(parse.json) { implicit request =>
    request.body.validate[TimeEntry].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      newEntry => {
        timeEntriesService.updateEntry(id, newEntry).map(entry => Ok(Json.toJson(entry)))
      }
    )
  }

  def getPendingEntries = SecuredAction.async {
    timeEntriesService.getPendingEntries.map(entries => Ok(Json.toJson(entries)))
  }

  def getEntriesByUser(userID: Long) = SecuredAction.async {
    timeEntriesService.getEntriesByUser(userID).map(entries => Ok(Json.toJson(entries)))
  }

}
