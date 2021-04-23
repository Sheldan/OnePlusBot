package dev.sheldan.oneplus.bot.modules.news.config;

import dev.sheldan.abstracto.core.config.PostTargetEnum;
import lombok.Getter;

@Getter
public enum NewsPostTarget implements PostTargetEnum {
    NEWS_TARGET("news");

    private String key;

    NewsPostTarget(String key) {
        this.key = key;
    }
}
