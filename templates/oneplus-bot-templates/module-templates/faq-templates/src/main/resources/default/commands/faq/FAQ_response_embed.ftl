{
  <#if additionalMessage??>
  "additionalMessage": "${additionalMessage?json_string}"
  </#if>
    "embeds": [
        {
            <#if description?? || imageURL??>
            <#include "user_author">
            <#if additionalMessage??>,</#if>
            <@user_author user=author/>
            <#if description??>
            ,"description": "${description?json_string}"
            </#if>
            ,"color" : {
                "r": "${red}",
                "g": "${green}",
                "b": "${blue}"
            }
            <#if imageURL??>
            ,"imageUrl": "${imageURL}"
            </#if>
            </#if>
        }
    ]
}