{
  <#include "abstracto_color">,
  "fields": [
      {
            "name": "<@safe_include "urban_search_command_response_embed_field_title_link"/>",
            "value": "${definition.url?json_string}",
            "inline": "true"
      },
      {
            "name": "<@safe_include "urban_search_command_response_embed_field_title_example"/>",
            "value": "${definition.example?json_string}",
            "inline": "true"
      }
  ],
  "additionalMessage": "${definition.definition?json_string}",
  "metaConfig": {
    "messageLimit": 1,
    "additionalMessageLengthLimit": 250
  }
}