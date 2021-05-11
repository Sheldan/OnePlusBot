package dev.sheldan.oneplus.bot.modules.setups.config;

import dev.sheldan.abstracto.core.config.PostTargetEnum;
import lombok.Getter;

@Getter
public enum SetupsPostTarget implements PostTargetEnum {
    SETUPS("setups");

    private String key;

    SetupsPostTarget(String key) {
        this.key = key;
    }
}
