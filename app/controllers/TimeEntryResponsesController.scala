package controllers

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{TimeEntryResponse, User}
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Action
import play.api.libs.concurrent.Execution.Implicits._
import services.responses.ResponsesService

import scala.concurrent.Future

class TimeEntryResponsesController (val messagesApi: MessagesApi,
                                    val env: Environment[User, JWTAuthenticator],
                                    responsesService: ResponsesService) extends Silhouette[User, JWTAuthenticator] {
  implicit val format = formatters.json.TimeEntryResponseFormats.restFormat

  def createResponse() = Action.async(parse.json) { implicit request =>
    request.body.validate[TimeEntryResponse].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
      response => {
        responsesService.createResponse(response).map(r => Ok(Json.toJson(r)))
      }
    )
  }

}
