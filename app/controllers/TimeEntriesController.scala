package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{TimeEntry, User, UserRoles}
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Action
import services.entries.TimeEntriesService

import scala.concurrent.Future

class TimeEntriesController @Inject() (val messagesApi: MessagesApi,
                                       val env: Environment[User, JWTAuthenticator],
                                       timeEntriesService: TimeEntriesService) extends Silhouette[User, JWTAuthenticator] {

  implicit val format = formatters.json.TimeEntryFormats.restFormat

  def createEntry() = Action.async(parse.json) { implicit request =>
    request.body.validate[TimeEntry].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      timeEntry => {
        timeEntriesService.createEntry(timeEntry).map(r => Ok(Json.toJson(r)))
      }
    )
  }

  def getEntry(id: Long) = Action.async {
    timeEntriesService.getEntryByID(id).map{
      case Some(entry) => Ok(Json.toJson(entry))
      case None => Ok(Json.toJson("null"))
    }
  }

  def editEntry(id: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[TimeEntry].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      entry => {
        val user = User(0, None, None,
          None, None, None,
          None, None, None, UserRoles.User)
        val newEntry = TimeEntry(0, user, entry.date, entry.quantity, entry.description, entry.status)
        timeEntriesService.updateEntry(id, newEntry).map(entry => Ok(Json.toJson(entry)))
      }
    )
  }

  def getPendingEntries = Action.async {
    timeEntriesService.getPendingEntries.map(entries => Ok(Json.toJson(entries)))
  }

}
