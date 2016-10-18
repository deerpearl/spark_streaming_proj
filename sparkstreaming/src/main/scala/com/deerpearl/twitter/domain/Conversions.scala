package com.deerpearl.twitter.domain


import com.deerpearl.{TweetsByDay, TweetsByTrack}
import org.apache.spark.streaming.Time
import org.joda.time.{DateTime, DateTimeZone}
import com.datastax.driver.core.utils.UUIDs

import twitter4j.Status

import com.deerpearl.sentiment.SentimentAnalyzer

/**
  * Created by deerpearl on 9/12/16.
  */
object Conversions {

  def toTweetsByDay(statusRDD: Status): TweetsByDay = {
//    val sen = SentimentAnalyzer.sentiment("")
    println("++++++++ sentiment : " + SentimentAnalyzer.mainSentiment(statusRDD.getText))

    val user = statusRDD.getUser
    println("++++++++ user location: " + user.getLocation)

    val geoLocation = Option(statusRDD.getGeoLocation)
    println("++++++++ user geolocation: " + statusRDD.getGeoLocation)


    TweetsByDay(
      id = statusRDD.getId.toString,
      userId = user.getId,
      userName = user.getName,
      userScreenName = user.getScreenName,
      userLocation = user.getLocation,
      createdTimestamp = formatMillis(statusRDD.getCreatedAt.getTime),
      //createdDay = formatMillis(statusRDD.getCreatedAt.getTime, "yyyyMMdd"),
      tweetText = statusRDD.getText,
      lang = statusRDD.getLang //statusRDD.getLang is not working. statusRDD.getUser().getLang() works
      //retweetCount = statusRDD.getRetweetCount,
      //favoriteCount = statusRDD.getFavoriteCount,
      //latitude = geoLocation map (_.getLatitude),
      //longitude = geoLocation map (_.getLongitude)
      )
  }

  def toTweetsByTrack(dateParts: Array[Int], track: String, count: Long): TweetsByTrack = {
    TweetsByTrack(
      id = UUIDs.timeBased(),
      track = track,
      year = dateParts(0),
      month = dateParts(1),
      day = dateParts(2),
      hour = dateParts(3),
      minute = dateParts(4),
      count = count)
  }

  def formatTime(time: Time, format: String = "yyyyMMddHH:mm:ss.SSS"): String =
    formatMillis(time.milliseconds, format)

  def formatMillis(millis: Long, format: String = "yyyyMMddHH:mm:ss.SSS"): String =
    new DateTime(millis, DateTimeZone.UTC).toString(format)
}
