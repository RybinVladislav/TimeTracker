package services.entries

import models.TimeEntry

import scala.concurrent.Future

trait TimeEntriesService {
  /**
    * Finds an entry by its entry id.
    *
    * @param entryID The id of the entry to find.
    * @return The found entryor None if no entry for the given ID could be found.
    */
  def getEntryByID(entryID: Long): Future[Option[TimeEntry]]

  /**
    * Gets the list of all entries
    *
    * @return The sequence of all entries.
    */
  def getAllEntries: Future[Seq[TimeEntry]]

  /**
    * Gets the list of all pending entries
    *
    * @return The sequence of all pending entries.
    */
  def getPendingEntries: Future[Seq[TimeEntry]]

  /**
    * Gets the list of all accepted entries by user
    *
    * @param userID The id of the user that created the entries.
    * @return The sequence of all accepted entries for a user with userID.
    */
  def getAcceptedEntriesByUser(userID: Long): Future[Seq[TimeEntry]]

  /**
    * Gets the list of all rejected entries by user
    *
    * @param userID The id of the user that created the entries.
    * @return The sequence of all rejected entries for a user with userID.
    */
  def getRejectedEntriesByUser(userID: Long): Future[Seq[TimeEntry]]

  /**
    * Creates an entry.
    *
    * @param entry The entry to create.
    * @return The created entry.
    */
  def createEntry(entry: TimeEntry): Future[String]

  /**
    * Updates an existing entry.
    *
    * @param entryID The id of entry to update.
    * @param entry New entry info.
    * @return The updated entry.
    */
  def updateEntry(entryID: Long, entry: TimeEntry): Future[String]
}
