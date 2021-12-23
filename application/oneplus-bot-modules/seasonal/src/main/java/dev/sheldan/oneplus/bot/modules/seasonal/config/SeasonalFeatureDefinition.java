package dev.sheldan.oneplus.bot.modules.seasonal.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum SeasonalFeatureDefinition implements FeatureDefinition {
    LIGHTS("lights");

    private String key;

    SeasonalFeatureDefinition(String key) {
        this.key = key;
    }
}
