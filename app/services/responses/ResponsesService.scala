package services.responses

import models.TimeEntryResponse

import scala.concurrent.Future

trait ResponsesService {
  /**
    * Creates a response.
    *
    * @param response The response to create.
    * @return The created response.
    */
  def createResponse(response: TimeEntryResponse): Future[Option[TimeEntryResponse]]

  /**
    * Finds a response by its id.
    *
    * @param responseID The id of the response to find.
    * @return The found response or None if no response for the given ID could be found.
    */
  def getResponseByID(responseID: Long): Future[Option[TimeEntryResponse]]

  /**
    * Gets the list of entry responses by entry id.
    *
    * @param entryID The id of the entry responses are addressed to.
    * @return The sequence of all entry responses.
    */
  def getResponsesByEntry(entryID: Long): Future[Seq[TimeEntryResponse]]
}
