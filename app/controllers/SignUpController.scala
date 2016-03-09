package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import _root_.services.users.UsersService
import security.{SignUp, Token}

import scala.concurrent.Future

/**
  * This controller manages registration of a user.
  */
class SignUpController @Inject() (val messagesApi: MessagesApi,
                                  val env: Environment[User, JWTAuthenticator],
                                  userService: UsersService,
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasher: PasswordHasher) extends Silhouette[User, JWTAuthenticator] {

  def signUp = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp].map {signUp =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
      userService.retrieve(loginInfo).flatMap {
        case None => {
          val authInfo = passwordHasher.hash(signUp.password)
          val userToSave = User(0, Some(loginInfo), signUp.)
          for {
            user <- userService.createUser(userToSave)
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- env.authenticatorService.create(loginInfo)
            token <- env.authenticatorService.init(authenticator)
            result <- env.authenticatorService.embed(token,
              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDateTime))))
          } yield {
            env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            env.eventBus.publish(LoginEvent(user, request, request2Messages))
            result
          }
        }
        case Some(user) => Future.successful(Conflict(Json.toJson("user already exists")))
      }
    }.recoverTotal {
      case error => Future.successful(BadRequest(Json.toJson(error)))
    }
  }

  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, Ok())
  }

}
