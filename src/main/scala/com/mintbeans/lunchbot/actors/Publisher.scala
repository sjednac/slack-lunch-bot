package com.mintbeans.lunchbot.actors

import akka.actor.{ActorLogging, Actor}
import com.flyberrycapital.slack.SlackClient
import com.mintbeans.lunchbot.actors.Publisher.{ChannelMessagePublished, ChannelMessage}

object Publisher {
  case class ChannelMessage(channel: String, text: String)
  case class ChannelMessagePublished(msg: ChannelMessage)
}

class Publisher(slack: SlackClient) extends Actor with ActorLogging {

  var publishedMessages = Set[ChannelMessage]()

  override def receive: Receive = {
    case msg: ChannelMessage => {
      publishedMessages.contains(msg) match {
        case true => sender() ! ChannelMessagePublished(msg)
        case false => {
          //NOTE: You'll need to invite your bot to the target channel (implied by as_user=true, which is required for
          //the bot name/icon to be displayed properly)
          slack.chat.postMessage(msg.channel, msg.text, Map("as_user" -> "true"))

          publishedMessages = publishedMessages + msg

          sender() ! ChannelMessagePublished(msg)
        }
      }
    }
  }
  
}
