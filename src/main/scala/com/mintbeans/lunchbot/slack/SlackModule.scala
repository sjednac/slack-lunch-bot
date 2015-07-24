package com.mintbeans.lunchbot.slack

import com.flyberrycapital.slack.SlackClient
import com.mintbeans.lunchbot.config.ConfigModule

trait SlackModule {
  this: ConfigModule =>

  lazy val slack = new SlackClient(config.getString("slack.apiToken")).connTimeout(config.getInt("slack.connectionTimeout")).readTimeout(config.getInt("slack.readTimeout"))

  lazy val slackChannel = config.getString("slack.channel")

}
