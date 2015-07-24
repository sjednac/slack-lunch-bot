package com.mintbeans.lunchbot.facebook

import com.mintbeans.lunchbot.config.ConfigModule
import com.mintbeans.lunchbot.facebook.Facebook.Page

import scala.collection.JavaConverters._

trait FacebookModule {
  this: ConfigModule =>

  lazy val facebook: Facebook = {
    val appId = config.getString("facebook.appId")
    val appSecret = config.getString("facebook.appSecret")

    new FacebookRestConnector(appId, appSecret)
  }

  lazy val facebookPages: Set[Page] = config.getConfigList("facebook.pages").asScala.map({ config =>
    Page(config.getString("id"), config.getString("label"))
  }).toSet
}
