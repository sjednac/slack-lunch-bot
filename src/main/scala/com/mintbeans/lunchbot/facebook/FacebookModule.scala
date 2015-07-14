package com.mintbeans.lunchbot.facebook

import com.mintbeans.lunchbot.config.ConfigModule

trait FacebookModule {
  this: ConfigModule =>

  lazy val facebook: Facebook = {
    val appId = config.getString("facebook.appId")
    val appSecret = config.getString("facebook.appSecret")

    new FacebookRestConnector(appId, appSecret)
  }

}
