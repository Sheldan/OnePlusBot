{
    <#assign roleMention="<@&479202891358535681>"/>
    "additionalMessage": "${roleMention}",
    "embeds": [
        {
            <#macro postDisplay post>
                ${post.subject?json_string}
                https://community.oneplus.com/thread/${post.postId?c}

            </#macro>
            "description": "<#list entries as entry><@postDisplay post=entry /></#list>"
        }
    ],
    "messageConfig": {
        "allowsRoleMention": true
    }
}