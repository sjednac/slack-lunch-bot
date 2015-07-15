name := "lunch-bot"

organization := "com.mintbeans"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

mainClass := Some("com.mintbeans.lunchbot.Main")

resolvers ++= Seq(
  "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe"              %   "config"                      % "1.2.1",
    "com.restfb"                %   "restfb"                      % "1.13.0",
    "com.flyberrycapital"       %%  "scala-slack"                 % "0.2.0",
    "ch.qos.logback"            %   "logback-classic"             % "1.1.1",
    "org.scalatest"             %%  "scalatest"                   % "2.2.5" % "test"
  )
}

enablePlugins(JavaAppPackaging)
