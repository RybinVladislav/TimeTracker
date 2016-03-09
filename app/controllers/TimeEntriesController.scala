package controllers

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import play.api.i18n.MessagesApi
import services.entries.TimeEntriesService

class TimeEntriesController (val messagesApi: MessagesApi,
                             val env: Environment[User, JWTAuthenticator],
                             timeEntriesService: TimeEntriesService) extends Silhouette[User, JWTAuthenticator] {

}
