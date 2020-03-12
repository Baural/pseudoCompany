name := "xls-from-http-save-to-elastic"

version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies ++= Dependencies.depends

enablePlugins(JavaAppPackaging)

packageName in Universal := name.value