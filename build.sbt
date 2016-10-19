import sbt.Keys._

name := "spark-streaming-example"
 
version := "1.0"

val spparkstreamingSettings = Seq(
  organization := "com.deerpearl",
  version := "0.1",
  scalaVersion := "2.11.7",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

)
//scalaVersion := "2.11.7"
 
resolvers += "jitpack" at "https://jitpack.io"

lazy val sparkStreamingDependencies = Seq (
  "org.apache.spark" % "spark-streaming_2.11" % "1.6.1"
    exclude("org.twitter4j", "twitter4j-stream") exclude("org.twitter4j", "twitter4j")
    exclude("org.twitter4j", "twitter4j-core")
  , "org.scalaj" %% "scalaj-http" % "2.2.1"
  , "org.jfarcand" % "wcs" % "1.5"
  , "joda-time" % "joda-time" % "2.3"
  //, "com.datastax.spark" %% "spark-cassandra-connector" % "1.5.0"
  , "org.joda" % "joda-convert" % "1.6"
  , "org.twitter4j" % "twitter4j-stream" % "4.0.5"
  //  "org.apache.kafka" % "kafka_2.10" % "0.8.0"
  //  "org.apache.kafka" % "kafka-clients" % "0.10.0.1"
  , "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0"
  , "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0"
  , "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models"
  , "org.twitter4j" % "twitter4j-core" % "4.0.5"
  , "com.datastax.spark" %% "spark-cassandra-connector" % "1.5.0"
  , "org.apache.spark" %% "spark-sql" % "1.4.0"
)

lazy val sparkAnalyticsDependencies = Seq (
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.0" // it is the one that is only needed for non-spark-sql code

  ,"com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-M3"
  , "org.apache.spark" % "spark-sql_2.11" % "2.0.1"
  , "org.apache.spark" % "spark-core_2.11" % "2.0.1"

)

lazy val sparkstreaming: Project = Project(
  id = "sparkstreaming",
  base = file("sparkstreaming")
).settings(spparkstreamingSettings:_*)
  .settings(libraryDependencies ++= (sparkStreamingDependencies ))


val spparkanalyticsSettings = Seq(
  organization := "com.deerpearl"
  , version := "0.1"
  , scalaVersion := "2.11.8"
  , scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

)
lazy val sparkanalytics = project.in(file("sparkanalytics"))
  .settings(spparkanalyticsSettings :_*)
  .settings(libraryDependencies ++= (sparkAnalyticsDependencies ))

lazy val main = project.in(file("."))
  .aggregate(sparkstreaming, sparkanalytics)


