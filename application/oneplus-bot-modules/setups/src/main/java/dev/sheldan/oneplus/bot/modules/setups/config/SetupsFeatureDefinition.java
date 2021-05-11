package dev.sheldan.oneplus.bot.modules.setups.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum SetupsFeatureDefinition implements FeatureDefinition {
    SETUPS("setups");

    private String key;

    SetupsFeatureDefinition(String key) {
        this.key = key;
    }
}