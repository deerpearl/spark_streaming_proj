package com.deerpearl


import akka.actor.Props
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.slf4j.LoggerFactory
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import twitter4j._
import com.deerpearl.common.config.ConfigRegistry._
import com.deerpearl.twitter.domain.Conversions._
import com.datastax.spark.connector.streaming._
import com.datastax.spark.connector.SomeColumns

import scala.language.postfixOps

trait SuperTwitterStreamingServices extends Serializable {

  val logger = LoggerFactory.getLogger(this.getClass)
/*
  val cassandraServices = new CassandraServices {}

  def createCassandraSchema(implicit cassandraConnector: CassandraConnector) =
    cassandraServices.createSchema(sparkCassandraKeyspace, cassandraCQLPath)
*/
    def createTwitterStream(
      filters: List[String] = Nil,
      storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER)(implicit ssc: StreamingContext) = {
    val authorization = new OAuthAuthorization(new ConfigurationBuilder()
        .setOAuthConsumerKey(twitterAuth.consumerKey)
        .setOAuthConsumerSecret(twitterAuth.consumerSecret)
        .setOAuthAccessToken(twitterAuth.accessToken)
        .setOAuthAccessTokenSecret(twitterAuth.accessTokenSecret)
        .build())

    ssc.actorStream[Status](
      Props(
        new SuperTwitterReceiverActorStream[Status](
          twitterAuth = authorization,
          filters = filters)),
      "TwitterStreamingReceiverActor",
      storageLevel)

  }

  def ingestTweets(topics: Set[String],
      windowSize: Duration,
      slideDuration: Duration)
      (implicit ssc: StreamingContext,
       dsStream: DStream[Status]) = {
    dsStream.print()

    // tweetsByTrack -> kafka
    //writeToKafka(tweetsByTrack)
/*
    val tweetsByDay: DStream[TweetsByDay] = getTweetsByDay(dsStream)

    // tweetsByDay -> streaming_tweets_by_day
    tweetsByDay.saveToCassandra(
      sparkCassandraKeyspace,
      "streaming_tweets_by_day",
      SomeColumns(
        "id",
        "user_id",
        "user_name",
        "user_screen_name",
        "created_timestamp",
        "created_day",
        "tweet_text",
        "lang",
        "retweet_count",
        "favorite_count",
        "latitude",
        "longitude"))
*/

    // tweetsByTrack -> streaming_tweets_by_track
    val tweetsByTrack: DStream[TweetsByTrack] = getTweetsByTrack(dsStream, topics, windowSize, slideDuration)

    tweetsByTrack.saveToCassandra(
      sparkCassandraKeyspace,
      "streaming_tweets_by_track",
      SomeColumns(
        "id",
        "track",
        "year",
        "month",
        "day",
        "hour",
        "minute",
        "count"))

    //val sparkCheckpoint = "hdfs://namenode:9000/checkpoint"

    println(dsStream.toString());
    val sparkCheckpoint = "_checkpoint"

    ssc.checkpoint(sparkCheckpoint)

    dsStream.saveAsTextFiles("newop") //Note: this does creating files and output to console

    ssc.start()
  }
/*
  def writeToKafka(dStream: DStream[TweetsByTrack]) =
    dStream.map(_.track).foreachRDD { rdd =>
      rdd foreachPartition { partition =>
        lazy val kafkaProducerParams = new Properties()

        val kafkaBootstrapServersFromEnv = sys.env.getOrElse("kafkaBootstrapServers", "")
        val kafkaProducerKeySerializerFromEnv = sys.env.getOrElse("kafkaProducerKeySerializer", "")
        val kafkaProducerValueSerializerFromEnv = sys.env.getOrElse("kafkaProducerValueSerializer", "")

        kafkaProducerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServersFromEnv)
        kafkaProducerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerKeySerializerFromEnv)
        kafkaProducerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerValueSerializerFromEnv)
        val producer = new KafkaProducer[String, String](kafkaProducerParams)

        partition foreach {
          case m: String =>
            val message = new ProducerRecord[String, String](kafkaTopicRaw, StaticValues.javaNull, m)
            producer.send(message)
          case _ => logger.warn("Unknown Partition Message!")
        }
      }
    }
*/

/*
  def getTweetsByDay(dsStream: DStream[Status]): DStream[TweetsByDay] = dsStream.map(toTweetsByDay)
*/
  def getTweetsByTrack(dsStream: DStream[Status],
      topics: Set[String],
      windowSize: Duration,
      slideDuration: Duration): DStream[TweetsByTrack] =
    dsStream
        .flatMap(_.getText.toLowerCase.split( """\s+"""))
        .filter(topics.contains)
        .countByValueAndWindow(windowSize, slideDuration)
        .transform {
          (rdd, time) =>
            val dateParts = formatTime(time, dateFormat)
                .split(dateFormatSplitter)
                .map(_.toInt)
            rdd map {
              case (track, count) =>
                toTweetsByTrack(dateParts, track, count)
            }
        }

}
