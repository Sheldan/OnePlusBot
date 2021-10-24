package dev.sheldan.oneplus.bot.custom.moderation.config;

import dev.sheldan.abstracto.core.config.PostTargetEnum;
import lombok.Getter;

@Getter
public enum ModerationCustomPostTarget implements PostTargetEnum {
    WARN_THRESHOLD_NOTIFICATION("warnThresholdNotification");

    private String key;

    ModerationCustomPostTarget(String key) {
        this.key = key;
    }
}
