package com.mintbeans.lunchbot.actors

import java.time.LocalDate

import akka.actor._
import com.flyberrycapital.slack.SlackClient
import com.mintbeans.lunchbot.actors.Announcer._
import com.mintbeans.lunchbot.actors.Publisher.{ChannelMessage, ChannelMessagePublished}
import com.mintbeans.lunchbot.actors.Scrapper.ScrappedPost
import com.mintbeans.lunchbot.facebook.Facebook
import com.mintbeans.lunchbot.facebook.Facebook.Page

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Announcer {
  object AnnounceLunchMenu
  object ResendChannelMessages
  object LunchAnnouncementTimeout

  implicit class SlackMessageFormat(sp: ScrappedPost) {
    def slackMessage: String = {
      val pic = sp.post.picture match {
        case None => ""
        case Some(link) => s"| _<${link}|picture>_"
      }

      s"""*### <https://www.facebook.com/${sp.page.id}|${sp.page.label}> ###*
         |${sp.post.message.getOrElse("No text content available.")} ${pic}
       """.stripMargin
    }
  }
}

class Announcer(lunchDuration: FiniteDuration, facebook: Facebook, pages: Set[Page], slack: SlackClient, channel: String) extends Actor with ActorLogging {
  require(!pages.isEmpty, "page list must not be empty!")

  override def receive: Receive = awaitingLunchPeriod

  def awaitingLunchPeriod: Receive = {
    case AnnounceLunchMenu => {
      log.info("Announcing {} delicious pages to {}. Lunch duration: {}", pages.size, channel, lunchDuration)

      val scrappers = pages.map(page => newScrapper(page))
      val publisher = newPublisher
      val queuedMessages = Set[ChannelMessage]()
      val resendSchedule = context.system.scheduler.schedule(2 seconds, 2 seconds, self, ResendChannelMessages)

      context.system.scheduler.scheduleOnce(lunchDuration, self, LunchAnnouncementTimeout)
      context.become(announcingLunch(scrappers, publisher, queuedMessages, resendSchedule))
    }
  }

  def announcingLunch(scrappers: Set[ActorRef], publisher: ActorRef, queuedMessages: Set[ChannelMessage], resendSchedule: Cancellable): Receive = {
    case post: ScrappedPost => {
      scrappers.contains(sender()) match {
        case false => {
          log.debug("Post already processed.")
          sender() ! PoisonPill
        }
        case true => {
          log.debug("Post from: {}. Pages left: {}", post.page.id, scrappers.size - 1)
          sender() ! PoisonPill

          val msg = ChannelMessage(channel, post.slackMessage)
          publisher ! msg

          context.become(announcingLunch(scrappers - sender(), publisher, queuedMessages + msg, resendSchedule))
        }
      }
    }
    case confirmation: ChannelMessagePublished => {
      val remainingMessages = queuedMessages - confirmation.msg
      if (scrappers.isEmpty && remainingMessages.isEmpty) {
        log.info("Lunch announcing finished.")
        publisher ! PoisonPill
        resendSchedule.cancel()
        context.become(awaitingLunchPeriod)
      } else {
        log.debug("Waiting for {} scrappers and {} message confirmations", scrappers.size, remainingMessages.size)
        context.become(announcingLunch(scrappers, publisher, remainingMessages, resendSchedule))
      }
    }
    case ResendChannelMessages => queuedMessages.foreach(msg => publisher ! msg)
    case LunchAnnouncementTimeout => {
      log.info("Lunch announcement timeout with {} scrappers still running and {} undelivered messages.", scrappers.size, queuedMessages.size)

      scrappers.foreach(scrapper => scrapper ! PoisonPill)
      publisher ! PoisonPill

      context.become(awaitingLunchPeriod)
    }
  }

  def newScrapper(page: Page): ActorRef = {
    val since = Some(LocalDate.now.atStartOfDay)
    val interval = 2 minutes

    context.actorOf(Props(classOf[Scrapper], self, facebook, page, since, interval))
  }

  def newPublisher: ActorRef = context.actorOf(Props(classOf[Publisher], slack))

}
