package security

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{User, UserRoles}
import play.api.i18n.Messages
import play.api.mvc.Request

import scala.concurrent.Future

object UserRights extends Authorization[User, JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)
                              (implicit request: Request[B], messages: Messages) = {
    Future.successful(identity.userRole == UserRoles.User)
  }
}
