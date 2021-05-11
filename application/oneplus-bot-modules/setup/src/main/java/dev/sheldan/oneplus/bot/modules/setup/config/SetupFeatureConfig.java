package dev.sheldan.oneplus.bot.modules.setup.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.PostTargetEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static dev.sheldan.oneplus.bot.modules.setup.listener.SetupListener.SETUP_UPVOTE_EMOTE_KEY;

@Component
public class SetupFeatureConfig implements FeatureConfig {

    @Override
    public FeatureDefinition getFeature() {
        return SetupFeatureDefinition.SETUP;
    }

    @Override
    public List<PostTargetEnum> getRequiredPostTargets() {
        return Arrays.asList(SetupPostTarget.SETUP);
    }

    @Override
    public List<String> getRequiredEmotes() {
        return Arrays.asList(SETUP_UPVOTE_EMOTE_KEY);
    }
}
