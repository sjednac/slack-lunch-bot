package com.mintbeans.lunchbot.actors

import java.time.LocalDateTime

import akka.actor.{ActorRef, ActorLogging, Actor}
import com.mintbeans.lunchbot.actors.Scrapper.{ScrappedPost, ScrapPage}
import com.mintbeans.lunchbot.facebook.Facebook
import com.mintbeans.lunchbot.facebook.Facebook.{Post, Page}

import scala.concurrent.duration.Duration.Zero
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Scrapper {
  object ScrapPage
  case class ScrappedPost(page: Page, post: Post)
}

class Scrapper(parent: ActorRef, facebook: Facebook, page: Page, since: Option[LocalDateTime], interval: FiniteDuration) extends Actor with ActorLogging {

  val scrappingSchedule = context.system.scheduler.schedule(Zero, interval, self, ScrapPage)

  override def postStop() = scrappingSchedule.cancel()

  override def receive: Receive = scrapping

  def scrapping: Receive = {
    case ScrapPage => facebook.lastPost(page, since) match {
      case None => log.debug("No post available yet ({}). Next poll in {}", page.id, interval)
      case Some(post) => {
        log.debug("Fetched post from {}", page.id)
        parent ! ScrappedPost(page, post)
      }
    }
  }

}
