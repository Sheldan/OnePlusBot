package dev.sheldan.oneplus.bot.modules.setup.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum SetupFeatureDefinition implements FeatureDefinition {
    SETUP("setup");

    private String key;

    SetupFeatureDefinition(String key) {
        this.key = key;
    }
}