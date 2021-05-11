package dev.sheldan.oneplus.bot.modules.referral.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum ReferralFeatureDefinition implements FeatureDefinition {
    REFERRAL("referral");

    private String key;

    ReferralFeatureDefinition(String key) {
        this.key = key;
    }
}