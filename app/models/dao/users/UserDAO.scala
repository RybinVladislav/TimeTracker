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
    * Finds a user by email.
    *
    * @param email The email of the user to find.
    * @return The found user or None if no user for the given email could be found.
    */
  def getUserByEmail(email: String): Future[Option[User]]

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
    * Updates a user.
    *
    * @param userID  Id of user to update.
    * @param newUser New user data.
    * @return The updated user.
    */
  def editUser(userID: Long, newUser: User): Future[Option[User]]

  /**
    * Activates a user.
    *
    * @param userID The id of user to activate.
    * @param loginInfo The login info to activate a user with.
    * @return The activated user.
    */
  def activateUser(userID: Long, loginInfo: LoginInfo): Future[Option[User]]

  /**
    * Creates an inactive user until fist login.
    *
    * @param user The user to create.
    * @return The created user.
    */
  def createInactiveUser(user: User): Future[Option[User]]

}
