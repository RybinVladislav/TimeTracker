package models.dao.entries

import java.sql.Date
import javax.inject.Inject

import models.{EntryStatus, TimeEntry}
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
    db.run(query.result.headOption).map {
      resultOption => resultOption.map {
        case (entry, entryUser) => DBTimeEntry.dbTimeEntry2TimeEntry(entry, entryUser)
      }
    }
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
    db.run(query.result).map {
      resultSeq => resultSeq.map {
        case (entry, entryUser) => DBTimeEntry.dbTimeEntry2TimeEntry(entry, entryUser)
      }
    }
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
    db.run(query.result).map {
      resultSeq => resultSeq.map {
        case (entry, entryUser) => DBTimeEntry.dbTimeEntry2TimeEntry(entry, entryUser)
      }
    }
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
  override def createEntry(entry: TimeEntry): Future[Option[TimeEntry]] = {
    db.run((slickTimeEntries returning slickTimeEntries.map(_.id)) += entry)
      .flatMap(id => getEntryByID(id))
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
    db.run(query.result).map {
      resultSeq => resultSeq.map {
        case (entry, entryUser) => DBTimeEntry.dbTimeEntry2TimeEntry(entry, entryUser)
      }
    }
  }
}
