package dev.sheldan.oneplus.bot.modules.faq.config;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import lombok.Getter;

@Getter
public enum FAQFeatureDefinition implements FeatureDefinition {
    FAQ("faq");

    private String key;

    FAQFeatureDefinition(String key) {
        this.key = key;
    }
}
