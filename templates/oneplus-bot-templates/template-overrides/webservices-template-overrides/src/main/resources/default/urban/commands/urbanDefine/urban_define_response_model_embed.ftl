{
    "additionalMessage": "${definition.definition?json_string}",
    "embeds": [
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
                    "valueLengthLimit": 250,
                    "inline": "true"
                }
            ]
        }
    ],
    "messageConfig": {
        "messageLimit": 1,
        "additionalMessageLengthLimit": 250
    }
}