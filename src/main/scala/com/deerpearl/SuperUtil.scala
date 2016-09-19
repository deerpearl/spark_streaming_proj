package com.deerpearl

import twitter4j.{StallWarning, Status, StatusDeletionNotice, StatusListener};
import com.deerpearl.common.config.ConfigRegistry._

object SuperUtil {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(twitterAuth.consumerKey)
    .setOAuthConsumerSecret(twitterAuth.consumerSecret)
    .setOAuthAccessToken(twitterAuth.accessToken)
    .setOAuthAccessTokenSecret(twitterAuth.accessTokenSecret)
    .build

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) { println(status.getText)
    }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
 }


}
