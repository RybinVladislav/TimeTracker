package models.dao.entries

import javax.inject.Inject

import models.{EntryStatus, TimeEntry, User}
import models.dao.DAOSlick
import org.joda.time.DateTime
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
          User(entryUser.id, null, entryUser.username, entryUser.firstName, entryUser.lastName, null, null, null, null, null),
          DateTime.parse(entry.date),
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
        User(entryUser.id, null, entryUser.username, entryUser.firstName, entryUser.lastName, null, null, null, null, null),
        DateTime.parse(entry.date),
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
          User(entryUser.id, null, entryUser.username, entryUser.firstName, entryUser.lastName, null, null, null, null, null),
          DateTime.parse(entry.date),
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
  override def getAcceptedEntriesByUser(userID: Long): Future[Seq[TimeEntry]] = {
    val query = {
      for {
        timeEntry <- slickTimeEntries.filter(dbEntry => dbEntry.status === EntryStatus.Accepted.toString && dbEntry.user_id === userID)
        timeEntryUser <- slickUsers.filter(_.id === userID)
      } yield (timeEntry, timeEntryUser)
    }
    db.run(query.result).map(resultOption => resultOption.map {
      case (entry, entryUser) =>
        TimeEntry(entry.id,
          User(entryUser.id, null, entryUser.username, entryUser.firstName, entryUser.lastName, null, null, null, null, null),
          DateTime.parse(entry.date),
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
  override def updateEntry(entryID: Long, newEntry: TimeEntry): Future[String] = {
    db.run(slickTimeEntries.filter(_.id === entryID)
      .map(oldEntry => (oldEntry.quantity, oldEntry.date, oldEntry.description, oldEntry.status))
      .update((newEntry.quantity, newEntry.date.toString, newEntry.description, newEntry.status.toString)))
      .map(res => "User successfully edited").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  /**
    * Creates an entry.
    *
    * @param entry The entry to create.
    * @return The created entry.
    */
  override def createEntry(entry: TimeEntry): Future[String] = {
    val dBTimeEntry = DBTimeEntry(entry.id, entry.user.id, entry.date.toString, entry.quantity, entry.description, entry.status.toString)
    db.run(slickTimeEntries += dBTimeEntry).map(res => "Entry successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
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
          User(entryUser.id, null, entryUser.username, entryUser.firstName, entryUser.lastName, null, null, null, null, null),
          DateTime.parse(entry.date),
          entry.quantity,
          entry.description,
          EntryStatus.withName(entry.status))
    }
    )
  }
}
