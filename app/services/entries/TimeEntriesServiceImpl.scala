package services.entries
import javax.inject.Inject

import models.TimeEntry
import models.dao.entries.TimeEntryDAO

import scala.concurrent.Future

class TimeEntriesServiceImpl @Inject() (timeEntriesDAO: TimeEntryDAO)extends TimeEntriesService{
  /**
    * Finds an entry by its entry id.
    *
    * @param entryID The id of the entry to find.
    * @return The found entryor None if no entry for the given ID could be found.
    */
  override def getEntryByID(entryID: Long): Future[Option[TimeEntry]] = timeEntriesDAO.getEntryByID(entryID)

  /**
    * Gets the list of all entries
    *
    * @return The sequence of all entries.
    */
  override def getAllEntries: Future[Seq[TimeEntry]] = timeEntriesDAO.getAllEntries

  /**
    * Gets the list of all rejected entries by user
    *
    * @param userID The id of the user that created the entries.
    * @return The sequence of all rejected entries for a user with userID.
    */
  override def getRejectedEntriesByUser(userID: Long): Future[Seq[TimeEntry]] = timeEntriesDAO.getRejectedEntriesByUser(userID)

  /**
    * Gets the list of all entries by user
    *
    * @param userID The id of the user that created the entries.
    * @return The sequence of all entries for a user with userID.
    */
  override def getEntriesByUser(userID: Long): Future[Seq[TimeEntry]] = timeEntriesDAO.getEntriesByUser(userID)

  /**
    * Updates an existing entry.
    *
    * @param entryID The id of entry to update.
    * @param entry   New entry info.
    * @return The updated entry.
    */
  override def updateEntry(entryID: Long, entry: TimeEntry): Future[Option[TimeEntry]] = timeEntriesDAO.updateEntry(entryID, entry)

  /**
    * Creates an entry.
    *
    * @param entry The entry to create.
    * @return The created entry.
    */
  override def createEntry(entry: TimeEntry): Future[String] = timeEntriesDAO.createEntry(entry)

  /**
    * Gets the list of all pending entries
    *
    * @return The sequence of all pending entries.
    */
  override def getPendingEntries: Future[Seq[TimeEntry]] = timeEntriesDAO.getPendingEntries
}
