{
    "embeds": [
        {
        <#include "abstracto_color">,
        "description": "
<#list uses as usage>
<#assign usage=usage>
<#assign commandName=usage.faqCommandName><@safe_include "FAQUsage_command_display"/>:
<#list usage.faqChannelGroupUsages as channelGroupUsage>
<#assign channelGroupUsage=channelGroupUsage>
<#assign channelGroupName=channelGroupUsage.channelGroupName>
<#assign uses=channelGroupUsage.uses>
<@safe_include "FAQUsage_usage_display"/><#sep>, </#list>
<#else>
<@safe_include "FAQUsage_no_usages"/></#list>"
        }
    ]
}