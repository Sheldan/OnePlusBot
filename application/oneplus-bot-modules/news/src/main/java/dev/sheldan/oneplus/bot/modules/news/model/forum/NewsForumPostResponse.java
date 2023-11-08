package dev.sheldan.oneplus.bot.modules.news.model.forum;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class NewsForumPostResponse {

    @SerializedName("data")
    private NewsForumPostData newsForumPostData;
}
