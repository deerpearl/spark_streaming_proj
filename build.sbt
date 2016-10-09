name := "spark-streaming-example"
 
version := "1.0"
 
scalaVersion := "2.11.7"
 
resolvers += "jitpack" at "https://jitpack.io"
 
libraryDependencies ++= Seq("org.apache.spark" % "spark-streaming_2.11" % "1.6.1"
  exclude("org.twitter4j", "twitter4j-stream") exclude("org.twitter4j", "twitter4j")
  exclude("org.twitter4j", "twitter4j-core")
  , "org.scalaj" %% "scalaj-http" % "2.2.1"
  , "org.jfarcand" % "wcs" % "1.5"
  , "joda-time" % "joda-time" % "2.3"
  , "com.datastax.spark" %% "spark-cassandra-connector" % "1.5.0"
  , "org.joda" % "joda-convert" % "1.6"
  , "org.twitter4j" % "twitter4j-stream" % "4.0.5"
  , "org.apache.spark" %% "spark-sql" % "1.4.0"
//  "org.apache.kafka" % "kafka_2.10" % "0.8.0"
//  "org.apache.kafka" % "kafka-clients" % "0.10.0.1"
  , "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0"
)

libraryDependencies +=  "org.twitter4j" % "twitter4j-core" % "4.0.5"
