package dev.sheldan.oneplus.bot.custom.moderation.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.PostTargetEnum;
import dev.sheldan.abstracto.moderation.config.feature.ModerationFeatureConfig;
import dev.sheldan.oneplus.bot.custom.moderation.service.ModModeServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class ModerationCustomFeature implements FeatureConfig {

    public static final String WARN_NOTIFICATION_THRESHOLD = "warnNotificationThreshold";

    @Autowired
    private ModerationFeatureConfig moderationFeatureConfig;

    @Override
    public FeatureDefinition getFeature() {
        return ModerationCustomFeatureDefinition.MODERATION_CUSTOM;
    }

    @Override
    public List<FeatureConfig> getRequiredFeatures() {
        return Arrays.asList(moderationFeatureConfig);
    }

    @Override
    public List<PostTargetEnum> getRequiredPostTargets() {
        return Arrays.asList(ModerationCustomPostTarget.WARN_THRESHOLD_NOTIFICATION);
    }

    @Override
    public List<String> getRequiredSystemConfigKeys() {
        return Arrays.asList(ModModeServiceBean.MODMODE_ROLE_CONFIG_KEY,
                ModModeServiceBean.MODMODE_CHANGED_ROLE_COLOR_CONFIG_KEY, WARN_NOTIFICATION_THRESHOLD);
    }
}
