# Slack Lunch Bot [![Build Status](https://travis-ci.org/sbilinski/slack-lunch-bot.svg?branch=master)](https://travis-ci.org/sbilinski/slack-lunch-bot) [![Codacy Badge](https://www.codacy.com/project/badge/f31422f8b52241fa877cb4fa90ee8507)](https://www.codacy.com/app/sbilinski/slack-lunch-bot)

A [Slack](http://slack.com) bot for fetching lunch menu announcements from selected **Facebook** pages.

## Installation

Use the [Universal](http://www.scala-sbt.org/sbt-native-packager/formats/universal.html#tasks) plugin to build a deployable artifact (check the link for other archive formats):

    sbt universal:package-bin

Copy it to a destination of your choice or `unzip` it directly as such:

    unzip -d /opt/ target/universal/slack-lunch-bot-0.1-SNAPSHOT.zip

Run the bot by executing the provided script:

    /opt/slack-lunch-bot-0.1-SNAPSHOT/bin/slack-lunch-bot

## Configuration

You can use the provided configuration file as a template:

    cp conf/application.conf.example conf/application.conf

### Slack integration

Setup a bot user account as described [here](https://api.slack.com/bot-users). Make sure, that the bot is a member of the target channel (e.g. `#lunch` or whatever you prefer).

Set the **api token** and the **channel name** in the `slack` section of the config.

### Facebook integration

Setup a Facebook application [here](https://developers.facebook.com). It will be used for querying the Facebook pages of your choice.

Set the **app id** and the **app secret** in the `facebook` section of the config. Put relevant page IDs in the same section (for `https://www.facebook.com/SomeRestaurant` the ID is simply `SomeRestaurant`).

### Querying policy

Check the `akka.quartz` section for a relevant cron expression. Once triggered, the bot will check each page for the latest Facebook post, that was made on that day (hopefully, it should be a menu), and publish it to Slack.
