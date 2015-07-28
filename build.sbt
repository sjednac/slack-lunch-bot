name := "slack-lunch-bot"

organization := "com.mintbeans"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8", "-language", "postfixOps")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

mainClass in Compile := Some("com.mintbeans.lunchbot.Main")

resolvers ++= Seq(
  "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val akkaVersion = "2.3.12"
  Seq(
    "com.typesafe"              %   "config"                      % "1.2.1",
    "com.restfb"                %   "restfb"                      % "1.13.0",
    "com.flyberrycapital"       %%  "scala-slack"                 % "0.2.0",
    "ch.qos.logback"            %   "logback-classic"             % "1.1.1",
    "com.typesafe.akka"         %%  "akka-actor"                  % akkaVersion,
    "com.typesafe.akka"         %%  "akka-slf4j"                  % akkaVersion,
    "com.typesafe.akka"         %%  "akka-testkit"                % akkaVersion   % "test",
    "com.enragedginger"         %%  "akka-quartz-scheduler"       % "1.4.0-akka-2.3.x",
    "junit"                     %   "junit"                       % "4.12"        % "test",
    "org.scalatest"             %%  "scalatest"                   % "2.2.5"       % "test"
  )
}

enablePlugins(JavaAppPackaging)

mappings in Universal <+= (packageBin in Compile, sourceDirectory ) map { (_, src) =>
    src / "main" / "resources" / "reference.conf" -> "conf/application.conf.example"
}

javaOptions in Universal ++= Seq(
  "-J-Xmx64m",
  "-J-Xms64m"
)

bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""
