package com.mintbeans.lunchbot.facebook

import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.{LocalDateTime, ZoneId}

import com.restfb.Version.VERSION_2_3
import com.restfb._
import com.restfb.types.Post

import scala.collection.JavaConverters._

class FacebookRestConnector(appId: String, appSecret: String) extends DefaultFacebookClient(VERSION_2_3) with Facebook {

  accessToken = obtainAppAccessToken(appId, appSecret).getAccessToken

  override def lastPost(pageId: String, since: Option[LocalDateTime] = None): Option[Facebook.Post] = {
    val feed = since match {
      case None => fetchConnection(s"${pageId}/posts", classOf[Post])
      case Some(date) => fetchConnection(s"${pageId}/posts", classOf[Post], Parameter.`with`("since", date.format(ISO_LOCAL_DATE_TIME)))
    }

    feed.getData.asScala.toList.headOption match {
      case None => None
      case Some(post) => {
        val time = post.getCreatedTime.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime
        val msg  = post.getMessage

        Some(Facebook.Post(time, Option(msg)))
      }
    }
  }

}
