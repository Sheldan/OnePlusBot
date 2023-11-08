{
    "embeds": [
        {
            <#macro postDisplay post>
                https://community.oneplus.com/thread/${post.postId?c} | ${post.subject?json_string}
            </#macro>
            "description": "<#list entries as entry><@postDisplay post=entry /></#list>"
        }
    ]
}