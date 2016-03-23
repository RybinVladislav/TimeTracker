package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{TimeEntry, User, UserRoles}
import play.api.Logger
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
  val accessLogger: Logger = Logger("access")

  def createEntry() = SecuredAction(UserRights).async(parse.json) { implicit request =>
    accessLogger.info("Entry creation requested")
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
    accessLogger.info(s"Entry $id requested")
    timeEntriesService.getEntryByID(id).map{
      case Some(entry) => Ok(Json.toJson(entry))
      case None => BadRequest(Json.obj("message" -> "Couldn't find entry."))
    }
  }

  def editEntry(id: Long) = SecuredAction.async(parse.json) { implicit request => {
    accessLogger.info(s"Entry $id edit requested")
    request.body.validate[TimeEntry].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      newEntry => {
        timeEntriesService.updateEntry(id, newEntry).map(entry => Ok(Json.toJson(entry)))
      }
    )
  }}

  def getPendingEntries = SecuredAction.async {
    accessLogger.info("All pending entries requested")
    timeEntriesService.getPendingEntries.map(entries => Ok(Json.toJson(entries)))
  }

  def getEntriesByUser(userID: Long) = SecuredAction.async {
    accessLogger.info(s"All entries by user $userID requested")
    timeEntriesService.getEntriesByUser(userID).map(entries => Ok(Json.toJson(entries)))
  }

}
