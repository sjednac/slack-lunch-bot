package com.mintbeans.lunchbot.facebook

import java.time.ZoneId

import com.restfb.Version.VERSION_2_3
import com.restfb._
import com.restfb.types.Post

import scala.collection.JavaConverters._

class FacebookRestConnector(appId: String, appSecret: String) extends DefaultFacebookClient(VERSION_2_3) with Facebook {

  accessToken = obtainAppAccessToken(appId, appSecret).getAccessToken

  override def lastPost(pageId: String): Facebook.Post = {
    val feed = fetchConnection(s"${pageId}/posts", classOf[Post])
    val post = feed.getData.asScala.head

    val time = post.getCreatedTime.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime
    val msg  = post.getMessage

    Facebook.Post(time, Option(msg))
  }

}
