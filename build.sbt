import sbt.Keys._

name := "mintenrichment"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  filters,

  "org.json" % "json" % "20160212",
  "org.postgresql" % "postgresql" % "9.4.1208",
  "org.mongodb" % "mongo-java-driver" % "2.13.2",
  "com.rabbitmq" % "amqp-client" % "3.6.1",

  "org.webjars" % "jquery" % "2.2.3",
  "org.webjars" % "bootstrap" % "3.3.6",

  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "xmlunit" % "xmlunit" % "1.6" % "test",
  "com.adrianhurt" % "play-bootstrap_2.11" % "1.0-P25-B3",
  "org.apache.jena" % "jena-arq" % "3.0.1"
)

resolvers ++= Seq(
  "Apache" at "http://repo1.maven.org/maven2/",
  "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
  "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

fork in run := false
fork in Test := true
