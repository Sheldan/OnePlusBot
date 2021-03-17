package dev.sheldan.oneplus.bot.custom.starboard.config;

import dev.sheldan.abstracto.core.config.PostTargetEnum;
import lombok.Getter;

@Getter
public enum StarboardCustomPostTarget implements PostTargetEnum {
    STARBOARD_NOTIFICATION("starboardNotification");

    private String key;

    StarboardCustomPostTarget(String key) {
        this.key = key;
    }
}
