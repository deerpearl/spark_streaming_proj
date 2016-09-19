package com.deerpearl

/**
  * Created by deerpearl on 9/5/16.
  */
case class TweetsByDay(id: String,
                       userId: Long, userName: String,
                       userScreenName: String,
                       createdTimestamp: String,
                       createdDay: String,
                       tweetText: String,
                       lang: String,
                       retweetCount: Int,
                       favoriteCount: Int,
                       latitude: Option[Double],
                       longitude: Option[Double])

case class TweetsByTrack(
                          id: Int,
                          track: String,
                          year: Int,
                          month: Int,
                          day: Int,
                          hour: Int,
                          minute: Int,
                          count: Long)


