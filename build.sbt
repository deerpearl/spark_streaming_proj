name := "spark-streaming-example"
 
version := "1.0"
 
scalaVersion := "2.11.7"
 
resolvers += "jitpack" at "https://jitpack.io"
 
libraryDependencies ++= Seq("org.apache.spark" % "spark-streaming_2.11" % "1.6.1"
  , "org.scalaj" %% "scalaj-http" % "2.2.1"
  , "org.jfarcand" % "wcs" % "1.5"
  , "joda-time" % "joda-time" % "2.3"
  , "com.datastax.spark" %% "spark-cassandra-connector" % "1.5.0"
  , "org.joda" % "joda-convert" % "1.6"
  , "org.twitter4j" % "twitter4j-stream" % "3.0.3"
//  , "org.apache.spark" %% "spark-catalyst_2.10" % "1.0.0"
  , "org.apache.spark" %% "spark-sql" % "1.4.0" 
)
