package models.dao

import java.sql.Date

import com.mohiva.play.silhouette.api.LoginInfo
import models._
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._

trait DBTableDefinitions {

  case class DBUser(id: Long,
                  username: Option[String],
                  firstName: Option[String],
                  lastName: Option[String],
                  address:Option[String],
                  phone: Option[String],
                  email: Option[String],
                  position: Option[String],
                  userRole: String)

  object DBUser {
    def dbUser2User(user: DBUser): User = {
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

  class UsersTable(tag: Tag) extends Table[DBUser](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[Option[String]]("username")
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def address = column[Option[String]]("address")
    def phone = column[Option[String]]("phone")
    def email = column[Option[String]]("email")
    def position = column[Option[String]]("position")
    def userRole = column[String]("user_role")
    def * = (id, username, firstName, lastName,
      address, phone, email, position, userRole) <> ((DBUser.apply _).tupled, DBUser.unapply)
  }

  case class DBLoginInfo(id: Option[Long],
                         providerID: String,
                         providerKey: String)

  class LoginInfosTable(tag: Tag) extends Table[DBLoginInfo](tag, "login_infos") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(userID: Long,
                             loginInfoId: Long)

  class UserLoginInfosTable(tag: Tag) extends Table[DBUserLoginInfo](tag, "user_login_infos") {
    def userID = column[Long]("user_id")
    def loginInfoId = column[Long]("login_info_id")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(hasher: String,
                            password: String,
                            salt: Option[String],
                            loginInfoId: Long)

  class PasswordInfosTable(tag: Tag) extends Table[DBPasswordInfo](tag, "password_infos") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("login_info_id")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBTimeEntry(id: Long,
                         user_id: Long,
                         date: Date,
                         quantity: Long,
                         description: String,
                         status: String)

  object DBTimeEntry {
    implicit def timeEntry2dbTimeEntry(entry: TimeEntry): DBTimeEntry = {
        DBTimeEntry(entry.id,
          entry.user.id,
          new Date(entry.date.toLong),
          entry.quantity,
          entry.description,
          entry.status.toString)
    }

    def dbTimeEntry2TimeEntry(entry: DBTimeEntry, user: DBUser): TimeEntry = {
      TimeEntry(entry.id,
        DBUser.dbUser2User(user),
        entry.date.getTime.toString,
        entry.quantity,
        entry.description,
        EntryStatus.withName(entry.status))
    }
  }

  class TimeEntriesTable(tag: Tag) extends Table[DBTimeEntry](tag, "time_entries") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def date = column[Date]("date")
    def quantity = column[Long]("quantity")
    def description = column[String]("description")
    def status = column[String]("status")
    def * = (id, user_id, date, quantity, description, status) <> ((DBTimeEntry.apply _).tupled, DBTimeEntry.unapply)
  }

  case class DBTimeEntryResponse(id: Long,
                                 manager_id: Long,
                                 entry_id: Long,
                                 date: Date,
                                 response: String)

  object DBTimeEntryResponse {
    implicit def timeEntryResponse2dbTimeEntryResponse(response: TimeEntryResponse): DBTimeEntryResponse = {
      DBTimeEntryResponse(response.id,
        response.manager.id,
        response.entry_id,
        new Date(response.date.toLong),
        response.response)
    }

    def dbTimeEntryResponse2TimeEntryResponse(response: DBTimeEntryResponse, manager: DBUser): TimeEntryResponse = {
      TimeEntryResponse(response.id,
        DBUser.dbUser2User(manager),
        response.entry_id,
        response.date.getTime.toString,
        response.response)
    }
  }

  class TimeEntryResponsesTable(tag: Tag) extends Table[DBTimeEntryResponse](tag, "time_entry_responses") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def manager_id = column[Long]("manager_id")
    def entry_id = column[Long]("entry_id")
    def date = column[Date]("date")
    def response = column[String]("response")
    def * = (id, manager_id, entry_id, date, response) <> ((DBTimeEntryResponse.apply _).tupled, DBTimeEntryResponse.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[UsersTable]
  val slickLoginInfos = TableQuery[LoginInfosTable]
  val slickUserLoginInfos = TableQuery[UserLoginInfosTable]
  val slickPasswordInfos = TableQuery[PasswordInfosTable]
  val slickTimeEntries = TableQuery[TimeEntriesTable]
  val slickResponses = TableQuery[TimeEntryResponsesTable]

  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID
                                        && dbLoginInfo.providerKey === loginInfo.providerKey)
}
