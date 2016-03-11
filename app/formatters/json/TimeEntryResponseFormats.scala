package formatters.json

import models.{TimeEntryResponse, User}
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._

object TimeEntryResponseFormats {
  val restFormat = {
    implicit val userFormat = formatters.json.UserFormats.restFormat

    val responseWrites = new Writes[TimeEntryResponse] {
      def writes(response: TimeEntryResponse) = Json.obj(
        "id" -> response.id,
        "manager" -> response.manager,
        "entry_id" -> response.entry_id,
        "date" -> response.date,
        "response" -> response.response
      )
    }

    val responseReads: Reads[TimeEntryResponse] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "manager").read[User] and
        (JsPath \ "entry_id").read[Long] and
        (JsPath \ "date").read[String] and
        (JsPath \ "response").read[String]
      )(TimeEntryResponse.apply _)

    Format(responseReads, responseWrites)
  }
}
