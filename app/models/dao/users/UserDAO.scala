package models.dao.users

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

/**
  * Gives access to the user object.
  */
trait UserDAO {

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def getUserByInfo(loginInfo: LoginInfo): Future[Option[User]]

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def getUserByID(userID: Long): Future[Option[User]]

  /**
    * Gets the list of all users
    *
    * @return The sequence of all registered users.
    */
  def getAllUsers: Future[Seq[User]]

  /**
    * Creates a user.
    *
    * @param user The user to create.
    * @return The created user.
    */
  def createUser(user: User): Future[User]

}
