{
  <#include "abstracto_color">,
  "fields": [
      {
            "name": "<@safe_include "urban_search_command_response_embed_field_title_link"/>",
            "value": "[<@safe_include "urban_search_command_response_embed_field_value_jump"/>](${definition.url?json_string})",
            "inline": "true"
      },
      {
            "name": "<@safe_include "urban_search_command_response_embed_field_title_example"/>",
            "value": "${definition.example?json_string}"
      }
  ],
  "timeStamp": "${definition.creationDate}",
  "additionalMessage": "${definition.definition?json_string}",
  "messageLimit": 1
}