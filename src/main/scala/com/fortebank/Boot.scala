package com.fortebank

import akka.actor._
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import com.fortebank.domain.PseudoCompany
import com.fortebank.serializers.{Json4sSerializers, SprayJsonSerializers}


object Boot extends App with AppConfig with SprayJsonSerializers with PostCodesWithSpray  {

  println("Boot start")

  /*  localPostCodeTest.map { _ =>
      println("Boot Done!")
      elastiClient.close()
      system.terminate()
    }*/

  sprayJsonPostCodeUpdate.map { _ =>
    println("Boot Done!")
    elastiClient.close()
    system.terminate()
  }

}

