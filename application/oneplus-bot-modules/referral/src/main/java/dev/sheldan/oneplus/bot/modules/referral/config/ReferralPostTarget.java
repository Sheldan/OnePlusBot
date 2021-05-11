package dev.sheldan.oneplus.bot.modules.referral.config;

import dev.sheldan.abstracto.core.config.PostTargetEnum;
import lombok.Getter;

@Getter
public enum ReferralPostTarget implements PostTargetEnum {
    REFERRAL("referral");

    private String key;

    ReferralPostTarget(String key) {
        this.key = key;
    }
}
