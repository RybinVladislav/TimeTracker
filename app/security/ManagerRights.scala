package security

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{User, UserRoles}
import play.api.i18n.{Lang, Messages}
import play.api.mvc.{Request, RequestHeader}

import scala.concurrent.Future

object ManagerRights extends Authorization[User, JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)
                           (implicit request: Request[B], messages: Messages) = {
    Future.successful(identity.userRole == UserRoles.Manager)
  }
}
