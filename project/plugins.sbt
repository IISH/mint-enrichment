// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6") //1.0.6 stops less from failing on compile.

addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "3.0.1")