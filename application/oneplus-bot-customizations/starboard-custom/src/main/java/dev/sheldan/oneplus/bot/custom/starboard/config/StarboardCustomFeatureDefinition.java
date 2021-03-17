package dev.sheldan.oneplus.bot.custom.starboard.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum StarboardCustomFeatureDefinition implements FeatureDefinition {
    STARBOARD_NOTIFICATION("starboardNotification");

    private String key;

    StarboardCustomFeatureDefinition(String key) {
        this.key = key;
    }
}