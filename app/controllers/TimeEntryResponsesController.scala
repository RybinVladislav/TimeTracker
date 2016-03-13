package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{TimeEntryResponse, User}
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Action
import play.api.libs.concurrent.Execution.Implicits._
import security.ManagerRights
import services.responses.ResponsesService

import scala.concurrent.Future

class TimeEntryResponsesController @Inject() (val messagesApi: MessagesApi,
                                              val env: Environment[User, JWTAuthenticator],
                                              responsesService: ResponsesService) extends Silhouette[User, JWTAuthenticator] {
  implicit val format = formatters.json.TimeEntryResponseFormats.restFormat

  def createResponse() = SecuredAction(ManagerRights).async(parse.json) { implicit request =>
    request.body.validate[TimeEntryResponse].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      response => {
        responsesService.createResponse(response).map(r => Ok(Json.toJson(r)))
      }
    )
  }

  def getResponse(id: Long) = SecuredAction.async {
    responsesService.getResponseByID(id).map{
      case Some(response) => Ok(Json.toJson(response))
      case None => BadRequest(Json.obj("message" -> "Couldn't find an entry"))
    }
  }

  def getResponsesByEntry(entryID: Long) = SecuredAction.async{
    responsesService.getResponsesByEntry(entryID).map(responses => Ok(Json.toJson(responses)))
  }
}
