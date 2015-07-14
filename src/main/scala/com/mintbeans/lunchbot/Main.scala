package com.mintbeans.lunchbot

import com.mintbeans.lunchbot.config.ConfigModule
import com.mintbeans.lunchbot.facebook.FacebookModule

object Main extends App with ConfigModule with FacebookModule {
  val marmolada = facebook.lastPost("MarmoladaChlebiKawa")
  println(s"Marmolada:\n${marmolada}\n")

  val lula  = facebook.lastPost("lulagdansk")
  println(s"Lula:\n${lula}\n")

  val otwARTa  = facebook.lastPost("otwArtagaleriasmaku")
  println(s"otwARTa:\n${otwARTa}\n")
}
