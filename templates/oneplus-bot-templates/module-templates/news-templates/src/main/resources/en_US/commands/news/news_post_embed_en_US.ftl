{
  <#assign roleMention="<@&479202891358535681>"/>
  <#assign authorMention>${author.user.name}#${author.user.discriminator}</#assign>
    "additionalMessage": "<@safe_include "news_post_description"/>",
    "embeds": [
        {
            <#if message.attachments?size gt 0>
            "imageUrl": "${message.attachments[0].proxyUrl}",
            </#if>
            "metaConfig": {
                "preventEmptyEmbed": true
            }
        }
    ],
    "messageConfig": {
        "allowsRoleMention": true
    }
}