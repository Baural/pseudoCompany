import sbt._

object Version {
  val ScalaTestVersion = "3.0.6"
  val AkkaVersion = "2.6.0"
  val AkkaHttpVersion = "10.1.10"
  val AlpakkaVersion = "1.1.2"
  val AlpakkaKafkaVersion = "1.1.0"
  val elastic4sVersion = "6.7.3"
  val json4sVersion = "3.6.7"
  val sprayJson = "1.3.5"
  val jodaTime = "2.10.5"
}


object Library {
  val akkaStreamAlpakkaElastic = "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % Version.AlpakkaVersion
  val akkaStreamAlpakkaCsv = "com.lightbend.akka" %% "akka-stream-alpakka-csv" % Version.AlpakkaVersion

  val akkaStreamKafka =  "com.typesafe.akka" %% "akka-stream-kafka" % Version.AlpakkaKafkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.AkkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % Version.AkkaHttpVersion
  // Used from Scala
  val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % Version.AkkaHttpVersion

  //"org.testcontainers" % "kafka" % "1.12.3"

  val akkaSlf4h = "com.typesafe.akka" %% "akka-slf4j" % Version.AkkaVersion
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  val elastic4sCore = "com.sksamuel.elastic4s" %% "elastic4s-core" % Version.elastic4sVersion
  val elastic4sHttpStreams =   "com.sksamuel.elastic4s" %% "elastic4s-http-streams" % Version.elastic4sVersion

  //json4s
  val json4sCore = "org.json4s" %% "json4s-core" % Version.json4sVersion
  val json4sJackson = "org.json4s" %% "json4s-jackson" % Version.json4sVersion
  val json4sNative = "org.json4s" %% "json4s-native" % Version.json4sVersion
  val akkaHttpJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.20.1"
  val jodaTime = "joda-time" % "joda-time" % Version.jodaTime
  val sprayJson = "io.spray" %%  "spray-json" % Version.sprayJson
}


object Dependencies {

  import Library._

  val depends = Seq(
    akkaStreamAlpakkaCsv,
    akkaStreamKafka,
    akkaStream,
    akkaHttp,
    akkaHttpSprayJson,
    //jackson1,
    //jackson2,
    akkaSlf4h,
    logback,
    akkaStreamAlpakkaElastic,
    json4sCore,
    json4sJackson,
    json4sNative,
    sprayJson,
    jodaTime
  )

}
