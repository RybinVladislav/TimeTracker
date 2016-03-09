package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, LoginEvent, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import services.users.UsersService
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import security.Token

import scala.concurrent.Future

/**
  * The credentials auth controller.
  *
  * @param messagesApi The Play messages API.
  * @param env The Silhouette environment.
  * @param userService The user service implementation.
  * @param authInfoRepository The auth info repository implementation.
  * @param credentialsProvider The credentials provider.
  * @param configuration The Play configuration.
  * @param clock The clock instance.
  */
class AuthController @Inject() (val messagesApi: MessagesApi,
                                val env: Environment[User, JWTAuthenticator],
                                userService: UsersService,
                                authInfoRepository: AuthInfoRepository,
                                credentialsProvider: CredentialsProvider,
                                configuration: Configuration,
                                clock: Clock) extends Silhouette[User, JWTAuthenticator] {

  implicit val restCredentialFormat = formatters.json.CredentialFormats.restFormat

  def authenticate = Action.async(parse.json[Credentials]) { implicit request =>
    credentialsProvider.authenticate(request.body).flatMap { loginInfo =>
      userService.retrieve(loginInfo).flatMap {
        case Some(user) => env.authenticatorService.create(user.loginInfo.get).flatMap { authenticator =>
          env.eventBus.publish(LoginEvent(user, request, request2Messages))
          env.authenticatorService.init(authenticator).flatMap { token =>
            env.authenticatorService.embed(token,
              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDateTime))))
          }
        }
        case None =>
          Future.failed(new IdentityNotFoundException("Couldn't find user"))
      }
    }.recoverWith(exceptionHandler)
  }
}
