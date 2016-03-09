package services.responses
import javax.inject.Inject

import models.TimeEntryResponse
import models.dao.responses.ResponseDAO

import scala.concurrent.Future

class ResponsesServiceImpl @Inject() (responsesDAO: ResponseDAO)extends ResponsesService{
  /**
    * Creates a response.
    *
    * @param response The response to create.
    * @return The created response.
    */
  override def createResponse(response: TimeEntryResponse): Future[String] = responsesDAO.createResponse(response)

  /**
    * Gets the list of entry responses by entry id.
    *
    * @param entryID The id of the entry responses are addressed to.
    * @return The sequence of all entry responses.
    */
  override def getResponsesByEntry(entryID: Long): Future[Seq[TimeEntryResponse]] = responsesDAO.getResponsesByEntry(entryID)

  /**
    * Finds a response by its id.
    *
    * @param responseID The id of the response to find.
    * @return The found response or None if no response for the given ID could be found.
    */
  override def getResponseByID(responseID: Long): Future[Option[TimeEntryResponse]] = responsesDAO.getResponseByID(responseID)
}
