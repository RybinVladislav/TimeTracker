package models.dao.users

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.{UserRoles, User}
import models.dao.DAOSlick
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.PostgresDriver.api._


import scala.concurrent.Future

class UserDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserDAO with DAOSlick {

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def getUserByInfo(loginInfo: LoginInfo): Future[Option[User]] = {

    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser

    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.id,
            Some(loginInfo),
            user.username,
            user.firstName,
            user.lastName,
            user.address,
            user.phone,
            user.email,
            user.position,
            UserRoles.withName(user.userRole))
      }
    }
  }

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def getUserByID(userID: Long): Future[Option[User]] = {

    val query = for {
      dbUser <- slickUsers.filter(_.id === userID)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(user.id,
            Some(LoginInfo(loginInfo.providerID, loginInfo.providerKey)),
            user.username,
            user.firstName,
            user.lastName,
            user.address,
            user.phone,
            user.email,
            user.position,
            UserRoles.withName(user.userRole))
      }
    }
  }

  /**
    * Finds a user by email.
    *
    * @param email The email of the user to find.
    * @return The found user or None if no user for the given email could be found.
    */
  def getUserByEmail(email: String): Future[Option[User]] = {

    val query = slickUsers.filter(_.email === email)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case user =>
          User(user.id,
            None,
            user.username,
            user.firstName,
            user.lastName,
            user.address,
            user.phone,
            user.email,
            user.position,
            UserRoles.withName(user.userRole))
      }
    }
  }

  /**
    * Activates a user.
    *
    * @param userID The id of user to activate.
    * @param loginInfo The login info to activate a user with.
    * @return The activated user.
    */
  def activateUser(userID: Long, loginInfo: LoginInfo): Future[Option[User]] = {
    val dbLoginInfo = DBLoginInfo(None, loginInfo.providerID, loginInfo.providerKey)

    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.
    val loginInfoAction = {

      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === loginInfo.providerID &&
          info.providerKey === loginInfo.providerKey).result.headOption

      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo

      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield loginInfo
    }

    // combine database actions to be run sequentially
    val actions = (for {
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(userID, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions)
    getUserByID(userID)
  }

  /**
    * Gets the list of all users without their loginInfo
    *
    * @return The sequence of all registered users.
    */
  def getAllUsers: Future[Seq[User]] = db.run(slickUsers.result).map { dbUserOption =>
    dbUserOption.map { user =>
      User(user.id,
        None,
        user.username,
        user.firstName,
        user.lastName,
        user.address,
        user.phone,
        user.email,
        user.position,
        UserRoles.withName(user.userRole))
    }
  }

  /**
    * Updates a user.
    *
    * @param userID  Id of user to update.
    * @param newUser New user data.
    * @return The updated user.
    */
  override def editUser(userID: Long, newUser: User): Future[Option[User]] = {
    db.run(slickUsers
      .filter(_.id === userID)
      .map(user => (user.firstName, user.lastName, user.address, user.phone, user.position))
      .update((newUser.firstName, newUser.lastName, newUser.address, newUser.phone, newUser.position)))
    getUserByID(userID)
  }

  /**
    * Creates an inactive user until fist login.
    *
    * @param user The user to create.
    * @return The created user.
    */
  override def createInactiveUser(user: User): Future[Option[User]] = {
    val dbUser = DBUser(0, user.username, user.firstName,
      user.lastName, user.address, user.phone, user.email, user.position, user.userRole.toString)
    db.run(slickUsers += dbUser)
    getUserByEmail(user.email.get)
  }
}
