{
  <#assign roleMention="<@&823285857515470868>"/>
  <#assign authorMention>${author.user.name}#${author.user.discriminator}</#assign>
  "additionalMessage": "<@safe_include "news_post_description"/>",
  <#if message.attachments?size gt 0>
    "imageUrl": "${message.attachments[0].proxyUrl}",
  </#if>
  "metaConfig": {
    "allowsRoleMention": true,
    "preventEmptyEmbed": true
  }
}