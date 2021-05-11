package dev.sheldan.oneplus.bot.modules.setup.config;

import dev.sheldan.abstracto.core.config.PostTargetEnum;
import lombok.Getter;

@Getter
public enum SetupPostTarget implements PostTargetEnum {
    SETUP("setup");

    private String key;

    SetupPostTarget(String key) {
        this.key = key;
    }
}
