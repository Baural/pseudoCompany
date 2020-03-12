package com.fortebank

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.javadsl.model.headers.HttpCredentials
import akka.http.scaladsl.ClientTransport
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import com.fortebank.Boot.system
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient


trait AppConfig {

  println("AppConfig start")

  implicit val system = ActorSystem("postcode-example")
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  val config: Config = ConfigFactory.load()
  val proxyHost: String = config.getConfig("application.proxy").getString("host")
  val proxyPort: Int = config.getConfig("application.proxy").getInt("port")
  val proxyUsername: String = config.getConfig("application.proxy").getString("username")
  val proxyPassword: String = config.getConfig("application.proxy").getString("password")

  val postCodesCsvUrlList: List[String] = List("M","Z")

  val proxyAddress = InetSocketAddress.createUnresolved(proxyHost, proxyPort)
  val auth = HttpCredentials.createBasicHttpCredentials(proxyUsername, proxyPassword)
  val httpsProxyTransport = ClientTransport.httpsProxy(proxyAddress, auth)



  implicit val settings = ConnectionPoolSettings(system)
    .withConnectionSettings(ClientConnectionSettings(system)
      .withTransport(httpsProxyTransport))

  val elasticHost: String = config.getConfig("elastic").getString("host")
  val elasticPort: Int = config.getConfig("elastic").getInt("port")

  implicit val elastiClient: RestClient = RestClient.builder(new HttpHost(elasticHost, elasticPort)).build()

  println("AppConfig done")
}
