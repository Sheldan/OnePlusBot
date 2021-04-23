package dev.sheldan.oneplus.bot.modules.news.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum NewsFeatureDefinition implements FeatureDefinition {
    NEWS("news");

    private String key;

    NewsFeatureDefinition(String key) {
        this.key = key;
    }
}