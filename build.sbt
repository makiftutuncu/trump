name := "trump"

version := "0.1"

scalaVersion := "2.12.7"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.5",
  "com.typesafe.akka" %% "akka-actor"  % "2.5.4",
  "com.typesafe.akka" %% "akka-stream" % "2.5.4"
)

addCommandAlias("c", "compile")
addCommandAlias("s", "scalastyle")
addCommandAlias("tc", "test:compile")
addCommandAlias("ts", "test:scalastyle")
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
