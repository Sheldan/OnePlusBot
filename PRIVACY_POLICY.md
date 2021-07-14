# r/oneplus Discord bot privacy policy

Last updated: 04.07.2021

## Description

The bot requires some information to function properly and in a reasonable way.
The detailed list of what information is stored and processed is the following:

### General
* your Discord user ID (in combination with the server ID) is used to uniquely identify you and associate various properties, such as experience, level, opened modmail threads etc
* the IDs of the servers this bot is in
* the IDs of the channel in the servers this bot is in
* the names of channel groups which were given by their creator
* the server aliases which were created for commands
* the name of emotes which are used in the bot for convenience, if they are customized
* towards which channel (identified by ID) certain messages by the bot are posted. e.g. logging, news, starboard
* **no message content, username, channel name or role name is stored, except at the places where its mentioned**
* a logfile is used in order to examine any malfunctions, the content is deleted after 7 days
* most of the stored records have a 'created' and 'updated' timestamp, in order to assist in examining bugs and malfunctions
* which commands have which cooldown in which channel group and in which channel group they are disabled
* which channel is in which channel group
* which role is allowed to execute which command
* which features are enabled
* which feature modes are enabled

### Moderation
* the date of the latest report via the reaction report system, in order to disallow quick reports
* mute reason, duration, mute date, who muted whom and in which message was the mute executed
* the names of filtered invite link servers in order to find out if it would be valid to allow the invite
* any configured allowed invite links the server ID and the actually used invite
    * this is necessary in order to determine the server via its ID and allow other unknown invite links. The invite link is necessary as there is no way to map server ID to actual server
* configured profanity regexes
    * reported profanities, including which message contains the profanity, and the message which was used to report the profanity, and whether it was identified as a true profanity
* messages and users which were reported via reaction reports and how many times they were reported, but not *who* reported a message
* **the text of notes regarding users**
    * this is used to enable taking notes about users, and the content is stored directly
* meta information regarding warnings
    * **reason for the warning**
    * date of the warning
    * the user who warned a user
    * whether the warning was decayed and when

### Embedded messages
* embedded message information
    * this information includes who embedded which message in which channel and is deleted after a few days

### Emote usage tracking
* the name of emotes which are being tracked in the emote usage tracking system for purely convenience reasons
* **who** used which emote is **not** tracked
* at which day an emote was used how many times

### News
* general information about news posts
    * the source message of the command, the created news post, and the author in order to enable the update mechanism

### Referral system
* the date of the latest referral post in order to enforce the referral bump mechanism

### Reminder
* **the message content** in order to provide you with the reminder text
* the date it was created, and the date it is due
* the id of the message which contained the command
* whether you have been reminded

### Starboard
* the message which was the origin for the starboard post
* the message which was the resulting starboard post
* the author of the message and the amount of stars
* who reacted to a starboard post
    * this is necessary to provide the information about 'top star giver' and to disallow duplicate starboard reactions

### Suggestion
* *the message content* of the message used to create the suggestion
    * this was used for the message used to update the status of a suggestion, but this is currently disabled
* the author of the suggestion and the message which has been posted in the suggestions channel
* every suggestion will be deleted completely from the database a few days after it has reached a final state (rejected, denied, accepted)

### Leveling system
* the amount of messages which were considered for the leveling system
    * it only considers a message once per minute, so it does not directly translate to your absolute message count
* the amount of experience, and the experience level you have
* whether experience gain has been disabled for you
* the role you received because of the experience system
* which roles are configured to be used as experience roles and at which level they are assigned
* which roles are used to disable experience gain

### FAQ
* the names of FAQ commands
* in which channel groups a FAQ command has a response
* the aliases of FAQ commands
* information about the actual FAQ message
    * the **content** of the message
    * the **URL** of the image used
    * the color of the embed to be used
    * the ID of the user to be used as author
* the amount of times a FAQ response has been used

### Assignable roles
* the names of assignable role places and assignable role button text, together with the associated emote markdown (if given)
* the assigned assignable roles for each member in order to provide the 'unique' assignable role functionality

## Grafana dashboard

There is also a [Grafana](https://grafana.com/) dashboard in order to inspect how the bot is operating.
The information visible in this dashboard is:

* message events
* Discord gateway ping
* starboard reactions
* amount of command executions
* emotes currently being processed for tracking
* embedded messages
* invite filter activity
* amount of experience which is currently being processed

All of this information cannot be linked to any user (or any server for that matter, if the bot would be in multiple servers) and is deleted after 15 days.


## How can I decide which information is collected?
It is not possible to opt-out of singular sub-services of the bot. Should you decide that your information should not be collected, please cease usage of the bot immediately (leave any guild the bot operates in).

_Should you decide to no longer utilize the bot, you may request your data to be erased within 30 days as per GDPR if you are a citizen of the EU. You can do this by sending an email to oneplus.appeals@pm.me with the subject: GDPR Data removal <Username#0000> <UserId>. If your request is incomplete, we cannot acknowledge it and therefore your data will not be removed. In order to identify authentic requests, please contact modmail beforehand by sending a direct message to the bot and stating your intention._


## Legal information
The bot is not an official application from OnePlus or OPPO. You agree to  use the bot at your own risk. The developers of the bot are not responsible for any damage done to your device, your computer or any other property.

We cannot promise you, as the user, to always update you when this Privacy Policy has changed. You can check the most recent version here. Important changes will be posted in the bot changelog channel of the r/oneplus Discord server.

OnePlus and OPPO are legal owners of OxygenOS. Visit https://oneplus.com/brand to view information about OnePlus.

## Open source content
This bot uses the following open source libraries and frameworks:

* [abstracto](https://github.com/Sheldan/abstracto) is used as a base for this bot, providing a lot of the functionalities
* [JDA](https://github.com/DV8FromTheWorld/JDA/) The Discord API Wrapper used
* [Spring boot](https://github.com/spring-projects/spring-boot) is used as a framework to create standalone application in Java with Java EE methods. (including Dependency injection and more)
* [Hibernate](https://github.com/hibernate/hibernate-orm) is used as a reference implementation of JPA.
* [Freemarker](https://github.com/apache/freemarker) is used as a templating engine. This is used to provide internationalization for user facing text and enable dynamic embed configuration.
* [Ehcache](https://github.com/ehcache/ehcache3) is used as a caching implementation.
* [Lombok](https://github.com/rzwitserloot/lombok) is used as a framework in order to speed up creation of container classes and builders.
* [Quartz](https://github.com/quartz-scheduler/quartz) is used as a scheduling framework in order to provide functionalities which either require a delayed or cronjob behaviour.
* [Docker](https://github.com/docker) is used to package the application into a container and [Docker Compose](https://github.com/docker/compose) is used to orchestrate the containers
* [Liquibase](https://github.com/liquibase/liquibase) is used to manage changes to the database
* [Prometheus](https://prometheus.io) to scrap and collect the metrics about how the bot is operating
* [Grafana](https://grafana.com) to visualize metrics of the bot
