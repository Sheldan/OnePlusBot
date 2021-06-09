package dev.sheldan.oneplus.bot.modules.faq.config;

import dev.sheldan.abstracto.core.config.FeatureMode;
import lombok.Getter;

@Getter
public enum FAQFeatureMode implements FeatureMode {
    FAQ_USES("faqUses");

    private final String key;

    FAQFeatureMode(String key) {
        this.key = key;
    }
}
