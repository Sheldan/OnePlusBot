package dev.sheldan.oneplus.bot.modules.news.model.forum;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class NewsForumPostDataRow {
    @SerializedName("id")
    private Long id;

    @SerializedName("subject")
    private String subject;

    @SerializedName("content")
    private String content;
}
