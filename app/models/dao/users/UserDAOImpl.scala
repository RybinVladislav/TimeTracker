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
    * Creates a user.
    *
    * @param user The user to create.
    * @return The created user.
    */
  def createUser(user: User): Future[User] = {
    val dbUser = DBUser(user.id, user.username, user.firstName, user.lastName, user.address,user.phone, user.email, user.position, user.userRole.toString)
    val dbLoginInfo = DBLoginInfo(None, user.loginInfo.get.providerID, user.loginInfo.get.providerKey)

    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.
    val loginInfoAction = {

      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.get.providerID &&
          info.providerKey === user.loginInfo.get.providerKey).result.headOption

      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo

      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield loginInfo
    }

    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(dbUser.id, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }

  /**
    * Gets the list of all users without their loginInfo
    *
    * @return The sequence of all registered users.
    */
  def getAllUsers: Future[Seq[User]] = db.run(slickUsers.result).map { dbUserOption =>
    dbUserOption.map { user =>
      User(user.id,
        null,
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
