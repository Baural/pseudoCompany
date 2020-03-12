package com.fortebank.serializers

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, Duration, LocalDate, Period}
import org.json4s._

trait DateTimeSerializer {

  val dateTimeFormat: String = "dd.MM.yyyy'T'HH:mm:ssZ"

  def dateTimeFormatter = DateTimeFormat.forPattern(dateTimeFormat)

  class DateTimeSerializer extends CustomSerializer[DateTime](format => ( {
    case JString(s) => DateTime.parse(s, dateTimeFormatter)
  }, {
    case dateTime: DateTime => JString(dateTime.toString(dateTimeFormatter))
  }
  ))

  // Date Serializer
  val dateFormat:String = "yyyy-MM-dd"

  def dateFormatter = DateTimeFormat.forPattern(dateFormat)

  class DateSerializer extends CustomSerializer[LocalDate](format => ( {
    case JString(s) => LocalDate.parse(s, dateFormatter)
  }, {
    case dateTime: LocalDate => JString(dateTime.toString(dateFormatter))
  }
  ))

  class DurationSerializer extends CustomSerializer[Duration](format => ( {
    case JString(s) => Duration.parse(s)
  }, {
    case d: Duration => JString(d.toString)
  }
  ))

  class PeriodSerializer extends CustomSerializer[Period](format => ( {
    case JString(s) => Period.parse(s)
  }, {
    case p: Period => JString(p.toString)
  }
  ))

  class ScalaDurationSerializer extends CustomSerializer[scala.concurrent.duration.Duration](format => ( {
    case JString(s) => scala.concurrent.duration.Duration(s)
  }, {
    case d: scala.concurrent.duration.Duration => JString(d.toString)
  }
  ))

  val allDateFormats = Seq(
    new DurationSerializer(),
    new PeriodSerializer(),
    new ScalaDurationSerializer()
  )

}