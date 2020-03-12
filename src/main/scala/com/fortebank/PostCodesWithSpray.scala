package com.fortebank

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.{Done, NotUsed}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, MediaRanges}
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.fortebank.domain.PseudoCompany
import com.fortebank.serializers.SprayJsonSerializers
import spray.json.{JsValue, JsonWriter}

import scala.concurrent.{ExecutionContextExecutor, Future}

trait PostCodesWithSpray {
  this: AppConfig with SprayJsonSerializers =>

  println("PostCodesWithSpray start")

  def system: ActorSystem
  def settings: ConnectionPoolSettings


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

  def toJson(map: Map[String, String])(
    implicit jsWriter: JsonWriter[Map[String, String]]): JsValue = jsWriter.write(map)

  def toObj(map: JsValue): PseudoCompany = {
    println(map)
    map.convertTo[PseudoCompany]
  }

  val str = ByteString(
    """НОВЫЙ ПОЧТОВЫЙ ИНДЕКС;БУКВЕННЫЙ КОД РЕГИОНА;ОБЛАСТЬ;ID ОБЛАСТИ;РАЙОН;ID РАЙОНА;ГОРОД;ID ГОРОДА;РАЙОН В ГОРОДЕ;ID РАЙОНА  В ГОРОДЕ;МИКРОРАЙОН;ID МИКРОРАЙОНА;УЛИЦА;ID УЛИЦЫ;ДОМ;ОБЛЫСЫ;АУДАНЫ;ҚАЛАСЫ;ҚАЛАНЫҢ ІШІНДЕГІ АУДАНЫ;ШАҒЫН АУДАНЫ;КӨШЕCI;ҮЙI;ПОЧТОВЫЙ ИНДЕКС
      |Z01C9H0;Z;;;;;ГОРОД Нур-Султан;A106724;РАЙОН В ГОРОДЕ Алматы;A107193;;;УЛИЦА Иван Панфилов;G251259;сооружение 55Т;;;ҚАЛАСЫ Нұр-Сұлтан;АУДАНЫ Алматы;;КӨШЕCI Иван Панфилов;құрылым 55Т;010010
      |Z01F7C9;Z;;;;;ГОРОД Нур-Султан;A106724;РАЙОН В ГОРОДЕ Алматы;A107193;;;УЛИЦА Иван Панфилов;G251259;дом 89;;;ҚАЛАСЫ Нұр-Сұлтан;АУДАНЫ Алматы;;КӨШЕCI Иван Панфилов;үй 89;010010""".stripMargin)

  val localPostCodeTest =
    Source
      .single(str)
      .via(CsvParsing.lineScanner(CsvParsing.SemiColon))
      .via(bytesToStringWithHeaders)
      .map(toJson)
      .map(toObj)
      .map(toElastic(_))
      .runWith(
        ElasticsearchSink.create[PseudoCompany](
          "postcode",
          typeName = "_doc"
        )
      )


  val sprayJsonPostCodeUpdate: Future[Done] =
    Source(postCodesCsvUrlList)
      .map(httpReq(_)) //Билдим httpRequest
      .mapAsync(1)(Http()(system).singleRequest(_, settings = settings)) //Делаем запрос и полцчаем HttpResponse
      .flatMapConcat(extractEntityData) //Получаем ByteString из HttpResponse
      .via(CsvParsing.lineScanner(CsvParsing.SemiColon)) //Построчно List[ByteString]
      .via(bytesToStringWithHeaders) //Map[String,String]
      .map(toJson) //Json
      .map(toObj) //В обьект
      .map(toElastic(_)) //В эластик
      .runWith(
        ElasticsearchSink.create[PseudoCompany](
          "pseudo_companies",
          typeName = "_companies"
        )
      )

  println("PseudoCompaniesWithSpray done")
}
