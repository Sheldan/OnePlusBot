{
    "embeds": [
        {
            "title": {
                "title": "<@safe_include "warning_threshold_reached_notification_title"/>"
            },
            <#include "abstracto_color">,
            <#assign warnedMemberMention=memberDisplay.memberMention>
            <#assign warnedUserId=memberDisplay.userId>
            <#assign memberMention=memberDisplay.memberMention>
            <#assign warnCount=warnCount>
            <#assign channelDisplay=channelDisplay>
            <#assign messageId=messageId>
            "description": "<@safe_include "warning_threshold_reached_notification_description"/>"
        }
    ]
}