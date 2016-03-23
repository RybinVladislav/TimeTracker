package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import _root_.services.users.UsersService
import play.api.Logger
import security.Token

import scala.concurrent.Future

/**
  * This controller manages registration of a user.
  */
class SignUpController @Inject() (val messagesApi: MessagesApi,
                                  val env: Environment[User, JWTAuthenticator],
                                  userService: UsersService,
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasher: PasswordHasher) extends Silhouette[User, JWTAuthenticator] {

  implicit val restCredentialFormat = formatters.json.CredentialFormats.restFormat
  val accessLogger: Logger = Logger("access")

  def signUp = Action.async(parse.json) { implicit request => {
    accessLogger.info("User registration requested")
    request.body.validate[Credentials].fold (
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      signUp => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
        userService.getUserByEmail(signUp.identifier).flatMap {
          case Some(u) =>
            userService.retrieve(loginInfo).flatMap {
              case None =>
                val authInfo = passwordHasher.hash(signUp.password)
                for {
                  user <- userService.activateUser(u.id, loginInfo)
                  authInfo <- authInfoRepository.add(loginInfo, authInfo)
                  authenticator <- env.authenticatorService.create(loginInfo)
                  token <- env.authenticatorService.init(authenticator)
                  result <- env.authenticatorService.embed(token,
                  Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDateTime))))
                } yield {
                  env.eventBus.publish(SignUpEvent(user.get, request, request2Messages))
                  env.eventBus.publish(LoginEvent(user.get, request, request2Messages))
                  result
                }
              case Some(user) => Future.successful(Conflict(Json.obj("message" -> "user already exists")))
            }
          case None => Future.successful(Conflict(Json.obj("message" -> "You are not allowed to sign up")))
        }
      }
    )
  }}

  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, Ok(views.html.index("")))
  }

}
