package com.fortebank.serializers

import com.fortebank.Boot.jsonFormat9
import com.fortebank.domain.PseudoCompany
import spray.json.{DefaultJsonProtocol, JsonFormat}

trait SprayJsonSerializers extends DefaultJsonProtocol{
  println("SprayJsonSerializers start")
  implicit val format: JsonFormat[PseudoCompany] = jsonFormat9(PseudoCompany)
  println("SprayJsonSerializers done")
}