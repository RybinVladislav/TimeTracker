package formatters.json

import models.{EntryStatus, TimeEntry, User}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object TimeEntryFormats {
  val restFormat = {
    implicit val enumFormat = EntryStatus.statusEnumFormat
    implicit val userFormat = formatters.json.UserFormats.restFormat

    val entryWrites = new Writes[TimeEntry] {
      def writes(entry: TimeEntry) = Json.obj(
        "id" -> entry.id,
        "user" -> entry.user,
        "date" -> entry.date,
        "quantity" -> entry.quantity,
        "description" -> entry.description,
        "status" -> entry.status
      )
    }

    val entryReads: Reads[TimeEntry] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "user").read[User] and
        (JsPath \ "date").read[String] and
        (JsPath \ "quantity").read[Long] and
        (JsPath \ "description").read[String] and
        (JsPath \ "status").read[EntryStatus.Value]
      )(TimeEntry.apply _)

    Format(entryReads, entryWrites)
  }
}
