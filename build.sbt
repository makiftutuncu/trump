name := "trump"

version := "0.1"

scalaVersion := "2.12.7"

scalacOptions += "-Ypartial-unification"

fork in Test := true
javaOptions in Test += "-Dconfig.resource=test.conf"

libraryDependencies ++= Seq(
  "com.typesafe"       % "config"          % "1.3.3",
  "com.typesafe.akka" %% "akka-http"       % "10.1.5",
  "com.typesafe.akka" %% "akka-actor"      % "2.5.4",
  "com.typesafe.akka" %% "akka-stream"     % "2.5.4",
  "de.heikoseeberger" %% "akka-http-circe" % "1.25.2",
  "io.circe"          %% "circe-core"      % "0.10.0",
  "org.scalacheck"    %% "scalacheck"      % "1.14.0"  % "test",
  "org.scalatest"     %% "scalatest"       % "3.0.5"   % "test"
)

addCommandAlias("c", "compile")
addCommandAlias("s", "scalastyle")
addCommandAlias("tc", "test:compile")
addCommandAlias("ts", "test:scalastyle")
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
