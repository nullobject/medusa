scalaVersion := "2.10.0"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "com.typesafe.akka" %% "akka-agent" % "2.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.1.0" % "test",
  "io.spray" % "spray-can" % "1.1-M7",
  "io.spray" % "spray-httpx" % "1.1-M7",
  "io.spray" %% "spray-json" % "1.2.3",
  "io.spray" % "spray-routing" % "1.1-M7",
  "org.scalatest" %% "scalatest" % "2.0.M5" % "test" cross CrossVersion.full,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.0" % "test"
)

seq(Revolver.settings: _*)

initialCommands in console := "import medusa._"
