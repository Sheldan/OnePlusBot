package dev.sheldan.oneplus.bot.modules.news.model.forum;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class NewsForumPostData {
    @SerializedName("rows")
    private List<NewsForumPostDataRow> rows;
}
