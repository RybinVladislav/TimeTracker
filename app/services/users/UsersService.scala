package services.users

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

import scala.concurrent.Future

trait UsersService extends IdentityService[User] {

  /**
    * Creates a user.
    *
    * @param user The user to create.
    * @return The created user.
    */
  def createUser(user: User): Future[User]

  /**
    * Gets the list of all users without their loginInfo
    *
    * @return The sequence of all registered users.
    */
  def getAllUsers: Future[Seq[User]]

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def getUserByID(userID: Long): Future[Option[User]]

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]]
}
