package com.fortebank.serializers

import org.json4s.native.Serialization
import org.json4s.{NoTypeHints, jackson}

trait Json4sSerializers extends DateTimeSerializer{
  implicit val formats = Serialization.formats( NoTypeHints ) +
    new DateTimeSerializer()

  implicit val serialization = jackson.Serialization
}