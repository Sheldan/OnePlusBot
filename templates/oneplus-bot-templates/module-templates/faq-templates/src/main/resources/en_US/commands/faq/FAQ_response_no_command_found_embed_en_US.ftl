{
  "additionalMessage": "<@safe_include "FAQ_response_no_command_found_message"/>
<#if availableCommands?size gt 0><#assign commandNames>`${availableCommands?join('`, `')}`</#assign><@safe_include "FAQ_response_no_command_found_message_available_commands"/><#else><@safe_include "FAQ_response_no_command_found_message_no_commands"/></#if>"
}