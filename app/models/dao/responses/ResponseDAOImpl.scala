package models.dao.responses

import java.sql.Date
import javax.inject.Inject

import models.{TimeEntryResponse, User, UserRoles}
import models.dao.DAOSlick
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class ResponseDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends ResponseDAO with DAOSlick {
  /**
    * Creates a response.
    *
    * @param response The response to create.
    * @return The created response.
    */
  override def createResponse(response: TimeEntryResponse): Future[String] = {
    val dBResponse = DBTimeEntryResponse(response.id, response.manager.id, response.entry_id, new Date(response.date.toLong), response.response)
    db.run(slickResponses += dBResponse).map(res => "Response successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  /**
    * Gets the list of entry responses by entry id.
    *
    * @param entryID The id of the entry responses are addressed to.
    * @return The sequence of all entry responses.
    */
  override def getResponsesByEntry(entryID: Long): Future[Seq[TimeEntryResponse]] = {
    val query = {
      for {
        entryResponse <- slickResponses.filter(_.entry_id === entryID)
        responseAuthor <- slickUsers.filter(_.id === entryResponse.manager_id)
      } yield (entryResponse, responseAuthor)
    }
    db.run(query.result).map(resultOption => resultOption.map {
      case (response, manager) =>
        TimeEntryResponse(response.id,
          User(manager.id, None, manager.username, manager.firstName, manager.lastName, None, None, None, None, UserRoles.Manager),
          response.entry_id,
          response.date.getTime.toString,
          response.response)
    }
    )
  }

  /**
    * Finds a response by its id.
    *
    * @param responseID The id of the response to find.
    * @return The found response or None if no response for the given ID could be found.
    */
  override def getResponseByID(responseID: Long): Future[Option[TimeEntryResponse]] = {
    val query = {
      for {
        entryResponse <- slickResponses.filter(_.id === responseID)
        responseAuthor <- slickUsers.filter(_.id === entryResponse.manager_id)
      } yield (entryResponse, responseAuthor)
    }
    db.run(query.result.headOption).map(resultOption => resultOption.map {
      case (response, manager) =>
        TimeEntryResponse(response.id,
          User(manager.id, None, manager.username, manager.firstName, manager.lastName, None, None, None, None, UserRoles.Manager),
          response.entry_id,
          response.date.getTime.toString,
          response.response)
    }
    )
  }
}
