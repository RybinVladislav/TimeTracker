package models

import org.joda.time.DateTime
import play.api.libs.json._

case class TimeEntry(id: Long,
                     user: User,
                     date: DateTime,
                     quantity: Long,
                     description: String,
                     status: EntryStatus.Value)

object EntryStatus extends Enumeration {
  type EntryStatus = Value
  val Pending, Rejected, Accepted = Value

  implicit val statusEnumFormat = new Format[EntryStatus.Value] {
    def reads(json: JsValue) = JsSuccess(EntryStatus.withName(json.as[String]))
    def writes(status: EntryStatus.Value) = JsString(status.toString)
  }
}
