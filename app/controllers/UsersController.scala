package controllers

import models._
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

object UsersController extends Controller {

  def getUser(id: Long) = Action.async { implicit request =>
    Users.get(id).map(user => Ok(Json.toJson(user)))
  }

  def getAllUsers = Action.async { implicit request =>
    Users.listAll.map(users => Ok(Json.toJson(users)))
  }

  def createUser() = Action.async(BodyParsers.parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"400", "message" -> JsError.toJson(errors))))
      },
      user => {
        Users.add(user).map(res => Ok(Json.obj("status" ->"OK", "message" -> ("User '"+ user.id +"' created."))))
      }
    )
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    Users.delete(id).map(res => Ok(Json.obj("status" ->"OK", "message" -> ("User '"+ res +"' deleted."))))
  }

  def editUser(id: Long) = Action.async(BodyParsers.parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"400", "message" -> JsError.toJson(errors))))
      },
      user => {
        Users.update(id, user).map(res => Ok(Json.obj("status" ->"OK", "message" -> ("User '"+ id +"' edited."))))
      }
    )
  }
}
