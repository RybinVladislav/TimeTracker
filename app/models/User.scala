package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future


case class User(id: Long, firstName: String, lastName: String,
                address:String, phone: String, email: String, position: String)

object User {
  implicit val userWrites = new Writes[User] {
    def writes(user: User) = Json.obj(
      "id" -> user.id,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "address" -> user.address,
      "phone" -> user.phone,
      "email" -> user.email,
      "position" -> user.position
    )
  }

  implicit val userReads: Reads[User] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "address").read[String] and
      (JsPath \ "phone").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "position").read[String]
    )(User.apply _)
}

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def address = column[String]("address")
  def phone = column[String]("phone")
  def email = column[String]("email")
  def position = column[String]("position")

  def * = (id, firstName, lastName, address, phone, email, position) <> ((User.apply _).tupled, User.unapply)
}

object Users {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val users = TableQuery[UsersTable]

  def add(user: User): Future[String] = {
    dbConfig.db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def update(id: Long, newUser: User): Future[String] = {
    dbConfig.db.run(users.filter(_.id === id)
                          .map(user => (user.firstName, user.lastName, user.position, user.address, user.email, user.phone))
                          .update((newUser.firstName, newUser.lastName, newUser.position, newUser.address, newUser.email, newUser.phone)))
                .map(res => "User successfully edited").recover {
                  case ex: Exception => ex.getCause.getMessage
                }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }
}

