package dev.sheldan.oneplus.bot.modules.news.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.PostTargetEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NewsFeature implements FeatureConfig {

    public static final String NEWS_FORUM_POST_NOTIFICATION_SERVER_ID_ENV_NAME = "NEWS_FORUM_POST_NOTIFICATION_SERVER_ID";

    @Override
    public FeatureDefinition getFeature() {
        return NewsFeatureDefinition.NEWS;
    }

    @Override
    public List<PostTargetEnum> getRequiredPostTargets() {
        return Arrays.asList(NewsPostTarget.NEWS_TARGET, NewsPostTarget.FORUM_POST_NOTIFICATION);
    }
}
