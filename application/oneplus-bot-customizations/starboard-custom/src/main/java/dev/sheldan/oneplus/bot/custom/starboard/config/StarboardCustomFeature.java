package dev.sheldan.oneplus.bot.custom.starboard.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.PostTargetEnum;
import dev.sheldan.abstracto.starboard.config.StarboardFeatureConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StarboardCustomFeature implements FeatureConfig {

    @Autowired
    private StarboardFeatureConfig starboardFeature;

    @Override
    public FeatureDefinition getFeature() {
        return StarboardCustomFeatureDefinition.STARBOARD_NOTIFICATION;
    }

    @Override
    public List<FeatureConfig> getRequiredFeatures() {
        return Arrays.asList(starboardFeature);
    }

    @Override
    public List<PostTargetEnum> getRequiredPostTargets() {
        return Arrays.asList(StarboardCustomPostTarget.STARBOARD_NOTIFICATION);
    }
}
