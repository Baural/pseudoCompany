package com.fortebank

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.{Done, NotUsed}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, MediaRanges}
import akka.http.scaladsl.model.headers.Accept
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.fortebank.domain.PseudoCompany
import com.fortebank.serializers.Json4sSerializers
import org.json4s.jackson.JsonMethods.parse
import org.json4s.native.Serialization.writePretty

import scala.concurrent.Future

trait PostCodesWithJson4s {
  this: AppConfig with Json4sSerializers =>

  implicit val system: ActorSystem

  def httpReq(code: String) = HttpRequest(uri = "https://api.post.kz/api/byRegion/fixed/"+code)
    .withHeaders(Accept(MediaRanges.`text/*`))

  def extractEntityData(response: HttpResponse): Source[ByteString, _] =
    response match {
      case HttpResponse(OK, _, entity, _) => entity.withoutSizeLimit().dataBytes
      case notOkResponse =>
        Source.failed(new RuntimeException(s"illegal response $notOkResponse"))
    }

  val bytesToStringWithHeaders: Flow[List[ByteString], Map[String, String], NotUsed]
  = CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "bin", "courtDecision", "illegalActivityStartDate", "ownerIin", "ownerName", "ownerRnn", "rnn", "taxpayerName", "taxpayerOrganization")


  def toElastic(post:PseudoCompany) = {
    WriteMessage.createUpsertMessage(post.bin, post)
  }

  /*  def toJson(map: Map[String, String])(
      implicit jsWriter: JsonWriter[Map[String, String]]): JsValue = jsWriter.write(map)*/


  def toJson2(map: Map[String, String]):String = {
    println("map to json str")
    println(map)
    writePretty(map)
  }

  def toObj(map: String): PseudoCompany = {
    println("str to obj")
    println(map)
    parse(map).extract[PseudoCompany]
  }

  /*
    val future2: Future[Done] =
      Source(postCodesCsvUrlList)
        //.single(str) //: HttpRequest
        //.single(httpReq) //: HttpRequest
        .map(httpReq(_))
        .mapAsync(1)(Http().singleRequest(_, settings = settings)) //: HttpResponse
        .flatMapConcat(extractEntityData) //: ByteString
        .via(CsvParsing.lineScanner(';')) //: List[ByteString]
        .via(bytesToStringWithHeaders)
        .map(toJson2)
        .map(toObj)
        .map(toElastic(_))
        .runWith(
          ElasticsearchSink.create[PostCode](
            "postcode",
            typeName = "_doc"
          )
        )*/

}
