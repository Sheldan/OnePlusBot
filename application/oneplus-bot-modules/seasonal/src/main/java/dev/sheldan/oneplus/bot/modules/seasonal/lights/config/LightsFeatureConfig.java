package dev.sheldan.oneplus.bot.modules.seasonal.lights.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.oneplus.bot.modules.seasonal.config.SeasonalFeatureDefinition;
import org.springframework.stereotype.Component;

@Component
public class LightsFeatureConfig implements FeatureConfig {

    @Override
    public FeatureDefinition getFeature() {
        return SeasonalFeatureDefinition.LIGHTS;
    }

}
