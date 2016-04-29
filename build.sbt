import sbt.Keys._

name := "mintenrichment"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.json" % "json" % "20160212",
  "org.postgresql" % "postgresql" % "9.4.1208",
  "org.mongodb" % "mongo-java-driver" % "2.13.2",
  "com.rabbitmq" % "amqp-client" % "3.6.1",

  filters,
  "org.webjars" % "jquery" % "2.2.3",
  "org.webjars" % "bootstrap" % "3.3.6",
  "com.rabbitmq" % "amqp-client" % "2.8.4"
)

resolvers ++= Seq(
  "Apache" at "http://repo1.maven.org/maven2/",
  "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
  "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)



fork in run := true
