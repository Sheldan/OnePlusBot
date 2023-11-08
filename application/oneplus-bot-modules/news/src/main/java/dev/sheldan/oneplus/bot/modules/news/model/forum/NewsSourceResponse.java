package dev.sheldan.oneplus.bot.modules.news.model.forum;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewsSourceResponse {
    @SerializedName("data")
    private NewsSourceUserData user;
}
