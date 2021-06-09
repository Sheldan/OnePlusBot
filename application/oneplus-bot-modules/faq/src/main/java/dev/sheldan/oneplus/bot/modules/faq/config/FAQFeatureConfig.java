package dev.sheldan.oneplus.bot.modules.faq.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.FeatureMode;
import dev.sheldan.abstracto.core.interactive.AutoDelayedAction;
import dev.sheldan.oneplus.bot.modules.faq.config.setup.GlobalChannelGroupDelayedActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FAQFeatureConfig implements FeatureConfig {

    @Autowired
    private GlobalChannelGroupDelayedActionConfig globalChannelGroupDelayedActionConfig;

    @Override
    public FeatureDefinition getFeature() {
        return FAQFeatureDefinition.FAQ;
    }

    @Override
    public List<AutoDelayedAction> getAutoSetupSteps() {
        return Arrays.asList(globalChannelGroupDelayedActionConfig);
    }

    @Override
    public List<FeatureMode> getAvailableModes() {
        return Arrays.asList(FAQFeatureMode.FAQ_USES);
    }
}
