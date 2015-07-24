package com.mintbeans.lunchbot.actors

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestProbe, TestKit}
import com.flyberrycapital.slack.Methods.Chat
import com.flyberrycapital.slack.SlackClient
import com.mintbeans.lunchbot.actors.Publisher.{ChannelMessagePublished, ChannelMessage}
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class PublisherSpec extends TestKit(ActorSystem("PublisherSpec")) with WordSpecLike with BeforeAndAfterAll with MockitoSugar {

  sealed trait SlackClientMock {
    val slack = mock[SlackClient]
    val chat = mock[Chat]

    when(slack.chat).thenReturn(chat)
  }

  sealed trait TestContext extends SlackClientMock {
    val channel = "#test"
    val text = "Hello World"
    val msg = ChannelMessage(channel, text)
    val confirmation = ChannelMessagePublished(msg)

    val probe = TestProbe()
  }

  override def afterAll() {
    system.shutdown()
  }

  "Publisher" should {
    "publish message to Slack with confirmation" in new TestContext {
      val publisher = system.actorOf(Props(classOf[Publisher], slack))

      publisher.tell(msg, probe.ref)
      probe.expectMsg(confirmation)

      verify(chat, times(1)).postMessage(Matchers.eq(channel), Matchers.eq(text), Matchers.any[Map[String, String]])
    }

    "publish each message only once" in new TestContext {
      val publisher = system.actorOf(Props(classOf[Publisher], slack))

      publisher.tell(msg, probe.ref)
      probe.expectMsg(confirmation)

      publisher.tell(msg, probe.ref)
      probe.expectMsg(confirmation)

      verify(chat, times(1)).postMessage(Matchers.eq(channel), Matchers.eq(text), Matchers.any[Map[String, String]])
    }

  }
}
