package dev.sheldan.oneplus.bot.modules.news.config;

import dev.sheldan.abstracto.core.config.FeatureMode;
import lombok.Getter;

@Getter
public enum NewsFeatureMode implements FeatureMode {
    AUTOMATIC_POST("automaticPost"),
    AUTOMATIC_PUBLISH("automaticPublish");

    private final String key;

    NewsFeatureMode(String key) {
        this.key = key;
    }
}
