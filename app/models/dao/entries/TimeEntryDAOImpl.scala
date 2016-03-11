package models.dao.entries

import java.sql.Date
import javax.inject.Inject

import models.{EntryStatus, TimeEntry, User, UserRoles}
import models.dao.DAOSlick
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class TimeEntryDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends TimeEntryDAO with DAOSlick {

  /**
    * Finds an entry by its entry id.
    *
    * @param entryID The id of the entry to find.
    * @return The found entry or None if no entry for the given ID could be found.
    */
  override def getEntryByID(entryID: Long): Future[Option[TimeEntry]] = {
    val query = {
      for {
        timeEntry <- slickTimeEntries.filter(_.id === entryID)
        timeEntryUser <- slickUsers.filter(_.id === timeEntry.user_id)
      } yield (timeEntry, timeEntryUser)
    }
    db.run(query.result.headOption).map(resultOption => resultOption.map {
      case (entry, entryUser) =>
        TimeEntry(entry.id,
          User(entryUser.id, None, entryUser.username, entryUser.firstName,
            entryUser.lastName, None, None, None, None, UserRoles.User),
          entry.date.getTime.toString,
          entry.quantity,
          entry.description,
          EntryStatus.withName(entry.status))
    }
    )
  }

  /**
    * Gets the list of all entries
    *
    * @return The sequence of all entries.
    */
  override def getAllEntries: Future[Seq[TimeEntry]] = {
    val query = {
      for {
        timeEntry <- slickTimeEntries
        timeEntryUser <- slickUsers.filter(_.id === timeEntry.user_id)
      } yield (timeEntry, timeEntryUser)
    }
    db.run(query.result).map(resultOption => resultOption.map{
      case (entry, entryUser) =>
      TimeEntry(entry.id,
        User(entryUser.id, None, entryUser.username, entryUser.firstName, entryUser.lastName, None, None, None, None, UserRoles.User),
        entry.date.getTime.toString,
        entry.quantity,
        entry.description,
        EntryStatus.withName(entry.status))
    }
    )
  }

  /**
    * Gets the list of all rejected entries by user
    *
    * @param userID The id of the user that created the entries.
    * @return The sequence of all rejected entries for a user with userID.
    */
  override def getRejectedEntriesByUser(userID: Long): Future[Seq[TimeEntry]] = {
    val query = {
      for {
        timeEntry <- slickTimeEntries.filter(dbEntry => dbEntry.status === EntryStatus.Rejected.toString && dbEntry.user_id === userID)
        timeEntryUser <- slickUsers.filter(_.id === userID)
      } yield (timeEntry, timeEntryUser)
    }
    db.run(query.result).map(resultOption => resultOption.map {
      case (entry, entryUser) =>
        TimeEntry(entry.id,
          User(entryUser.id, None, entryUser.username, entryUser.firstName, entryUser.lastName, None, None, None, None, UserRoles.User),
          entry.date.getTime.toString,
          entry.quantity,
          entry.description,
          EntryStatus.withName(entry.status))
    }
    )
  }

  /**
    * Gets the list of all accepted entries by user
    *
    * @param userID The id of the user that created the entries.
    * @return The sequence of all accepted entries for a user with userID.
    */
  override def getEntriesByUser(userID: Long): Future[Seq[TimeEntry]] = {
    val query = {
      for {
        timeEntry <- slickTimeEntries.filter(dbEntry =>
          dbEntry.user_id === userID)
        timeEntryUser <- slickUsers.filter(_.id === userID)
      } yield (timeEntry, timeEntryUser)
    }
    db.run(query.result).map(resultOption => resultOption.map {
      case (entry, entryUser) =>
        TimeEntry(entry.id,
          User(entryUser.id, None, entryUser.username, entryUser.firstName,
            entryUser.lastName, None, None, None, None, UserRoles.User),
          entry.date.getTime.toString,
          entry.quantity,
          entry.description,
          EntryStatus.withName(entry.status))
    }
    )
  }

  /**
    * Updates an existing entry.
    *
    * @param entryID The id of entry to update.
    * @param newEntry  New entry info.
    * @return The updated entry.
    */
  override def updateEntry(entryID: Long, newEntry: TimeEntry): Future[Option[TimeEntry]] = {
    db.run(slickTimeEntries.filter(_.id === entryID)
      .map(oldEntry => (oldEntry.quantity, oldEntry.date, oldEntry.description, oldEntry.status))
      .update((newEntry.quantity, new Date(newEntry.date.toLong), newEntry.description, newEntry.status.toString)))
    getEntryByID(entryID)
  }

  /**
    * Creates an entry.
    *
    * @param entry The entry to create.
    * @return The created entry.
    */
  override def createEntry(entry: TimeEntry): Future[String] = {
    val dBTimeEntry = DBTimeEntry(entry.id, entry.user.id, new Date(entry.date.toLong), entry.quantity, entry.description, entry.status.toString)
    db.run(slickTimeEntries += dBTimeEntry).map(res => "Entry successfully added").recover {
      case ex: Exception => ex.toString
    }
  }

  /**
    * Gets the list of all pending entries
    *
    * @return The sequence of all pending entries.
    */
  override def getPendingEntries: Future[Seq[TimeEntry]] = {
    val query = {
      for {
        timeEntry <- slickTimeEntries.filter(_.status === EntryStatus.Pending.toString)
        timeEntryUser <- slickUsers.filter(_.id === timeEntry.user_id)
      } yield (timeEntry, timeEntryUser)
    }
    db.run(query.result).map(resultOption => resultOption.map {
      case (entry, entryUser) =>
        TimeEntry(entry.id,
          User(entryUser.id, None, entryUser.username, entryUser.firstName,
            entryUser.lastName, None, None, None, None, UserRoles.User),
          entry.date.getTime.toString,
          entry.quantity,
          entry.description,
          EntryStatus.withName(entry.status))
    }
    )
  }
}
