package dev.sheldan.oneplus.bot.modules.setups.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.PostTargetEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static dev.sheldan.oneplus.bot.modules.setups.listener.SetupsListener.SETUPS_UPVOTE_EMOTE_KEY;

@Component
public class SetupsFeature implements FeatureConfig {

    @Override
    public FeatureDefinition getFeature() {
        return SetupsFeatureDefinition.SETUPS;
    }

    @Override
    public List<PostTargetEnum> getRequiredPostTargets() {
        return Arrays.asList(SetupsPostTarget.SETUPS);
    }

    @Override
    public List<String> getRequiredEmotes() {
        return Arrays.asList(SETUPS_UPVOTE_EMOTE_KEY);
    }
}
