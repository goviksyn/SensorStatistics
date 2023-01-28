name := "SensorStatistics"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.7.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.4",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "com.github.tototoshi" %% "scala-csv" % "1.3.10",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)

scalacOptions := Seq(
  "-deprecation"
)

scalacOptions in (Compile, doc) := Seq("-doc-title", "SensorStatistics", "-doc-version", version.value)
