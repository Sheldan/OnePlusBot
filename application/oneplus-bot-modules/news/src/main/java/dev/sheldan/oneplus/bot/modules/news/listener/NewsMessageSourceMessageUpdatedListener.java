package dev.sheldan.oneplus.bot.modules.news.listener;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.listener.async.jda.AsyncMessageTextUpdatedListener;
import dev.sheldan.abstracto.core.models.listener.MessageTextUpdatedModel;
import dev.sheldan.oneplus.bot.modules.news.config.NewsFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsPost;
import dev.sheldan.oneplus.bot.modules.news.service.NewsServiceBean;
import dev.sheldan.oneplus.bot.modules.news.service.management.NewsPostManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class NewsMessageSourceMessageUpdatedListener implements AsyncMessageTextUpdatedListener {

    @Autowired
    private NewsPostManagementServiceBean newsPostManagementServiceBean;

    @Autowired
    private NewsServiceBean newsServiceBean;

    @Override
    public DefaultListenerResult execute(MessageTextUpdatedModel model) {
        Optional<NewsPost> existingPostOptional = newsPostManagementServiceBean.getNewsPostForSourceMessage(model.getAfter().getIdLong());
        if(existingPostOptional.isPresent()) {
            NewsPost newsPost = existingPostOptional.get();
            if(!newsPost.isLocked()) {
                newsServiceBean.updateNewsPost(newsPost, model.getAfter());
                return DefaultListenerResult.PROCESSED;
            } else {
                log.info("Not updating news post {}, because it is locked.", newsPost.getSourceMessageId());
            }
        }
        return DefaultListenerResult.IGNORED;
    }

    @Override
    public FeatureDefinition getFeature() {
        return NewsFeatureDefinition.NEWS;
    }
}
