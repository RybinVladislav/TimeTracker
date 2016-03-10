package services.users

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.dao.users.UserDAO

import scala.concurrent.Future

/**
  * Handles actions to users.
  *
  * @param userDAO The user DAO implementation.
  */
class UsersServiceImpl @Inject() (userDAO: UserDAO) extends UsersService {

  /**
    * Creates a user.
    *
    * @param user The user to create.
    * @return The created user.
    */
  override def createInactiveUser(user: User): Future[Option[User]] = userDAO.createInactiveUser(user)

  /**
    * Activates a user.
    *
    * @param userID The id of user to activate.
    * @param loginInfo The login info to activate a user with.
    * @return The activated user.
    */
  def activateUser(userID: Long, loginInfo: LoginInfo): Future[Option[User]] = userDAO.activateUser(userID, loginInfo)

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.getUserByInfo(loginInfo)

  /**
    * Gets the list of all users without their loginInfo
    *
    * @return The sequence of all registered users.
    */
  override def getAllUsers: Future[Seq[User]] = userDAO.getAllUsers

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  override def getUserByID(userID: Long): Future[Option[User]] = userDAO.getUserByID(userID)

  /**
    * Finds a user by email.
    *
    * @param email The email of the user to find.
    * @return The found user or None if no user for the given email could be found.
    */
  def getUserByEmail(email: String): Future[Option[User]] = userDAO.getUserByEmail(email)

  /**
    * Updates a user.
    *
    * @param userID  Id of user to update.
    * @param newUser New user data.
    * @return The updated user.
    */
  override def editUser(userID: Long, newUser: User): Future[Option[User]] = userDAO.editUser(userID, newUser)
}
