name := "trump"

version := "0.1"

scalaVersion := "2.12.7"

scalacOptions += "-Ypartial-unification"

fork in Test := true
javaOptions in Test += "-Dconfig.resource=test.conf"

libraryDependencies ++= Seq(
  "ch.qos.logback"              % "logback-classic" % "1.2.3",
  "com.typesafe"                % "config"          % "1.3.3",
  "com.typesafe.akka"          %% "akka-http"       % "10.1.5",
  "com.typesafe.akka"          %% "akka-actor"      % "2.5.4",
  "com.typesafe.akka"          %% "akka-stream"     % "2.5.4",
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.0",
  "de.heikoseeberger"          %% "akka-http-circe" % "1.25.2",
  "io.circe"                   %% "circe-core"      % "0.10.0",
  "io.circe"                   %% "circe-parser"    % "0.10.0",
  "net.debasishg"              %% "redisclient"     % "3.9",
  "org.scalacheck"             %% "scalacheck"      % "1.14.0"  % "test",
  "org.scalatest"              %% "scalatest"       % "3.0.5"   % "test"
)

addCommandAlias("c", "compile")
addCommandAlias("s", "scalastyle")
addCommandAlias("tc", "test:compile")
addCommandAlias("ts", "test:scalastyle")
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
