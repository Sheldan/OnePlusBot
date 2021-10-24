{
    <#include "abstracto_color">,
    "description": "
    <@safe_include "listFAQCommands_command_header"/>

<#list commands as command>
<#assign command=command>
<#assign aliases>${command.aliases?join(', ')}</#assign>
<#assign commandName=command.commandName><@safe_include "listFAQCommands_command_display"/>
<#if command.aliases?size gt 0> <@safe_include "listFAQCommands_channel_group_display_aliases"/></#if>
<@safe_include "listFAQCommands_channel_groups_header"/>
<#list command.channelGroups as channelGroup>
<#assign channelGroup=channelGroup>
<#assign channelGroupName=channelGroup.channelGroupName>
<#assign responseCount=channelGroup.responseCount>
<@safe_include "listFAQCommands_channel_group_display"/><#sep>, </#list>

<#else>
<@safe_include "listFAQCommands_no_commands"/></#list>"
}