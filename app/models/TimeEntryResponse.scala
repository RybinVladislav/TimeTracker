package models

import org.joda.time.DateTime

/**
  * The manager response object.
  *
  * @param id The unique ID of the response.
  * @param manager The linked manager user.
  * @param entry_id The id of the corresponding time entry.
  * @param response The message of the manager.
  * @param date Response creation date.
  *
  */
case class TimeEntryResponse(id: Long,
                             manager: User,
                             entry_id: Long,
                             date: DateTime,
                             response: String)