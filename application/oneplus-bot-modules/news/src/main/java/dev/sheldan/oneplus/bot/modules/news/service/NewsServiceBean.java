package dev.sheldan.oneplus.bot.modules.news.service;

import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.PostTargetService;
import dev.sheldan.abstracto.core.templating.model.MessageToSend;
import dev.sheldan.abstracto.core.templating.service.TemplateService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.oneplus.bot.modules.news.config.NewsPostTarget;
import dev.sheldan.oneplus.bot.modules.news.exception.NewsPostLockedException;
import dev.sheldan.oneplus.bot.modules.news.model.NewsMessageModel;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsPost;
import dev.sheldan.oneplus.bot.modules.news.service.management.NewsPostManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class NewsServiceBean {

    @Autowired
    private PostTargetService postTargetService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private NewsPostManagementServiceBean newsPostManagementServiceBean;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private NewsServiceBean self;

    private static final String MESSAGE_TEMPLATE_KEY = "news_post";

    @Value("${abstracto.feature.news.postLockSeconds}")
    private Long postLockSeconds;

    @Value("${abstracto.feature.news.removalDays}")
    private Long removalDays;

    @Transactional
    public void lockNewsPosts() {
        Instant oldestDate = Instant.now().minus(postLockSeconds, ChronoUnit.SECONDS);
        log.info("Locking news posts older than {}.", oldestDate);
        List<NewsPost> oldPosts = newsPostManagementServiceBean.findNewsPostsOlderNotLocked(oldestDate);
        log.info("Locking {} news posts.", oldPosts.size());
        oldPosts.forEach(newsPost -> newsPost.setLocked(true));
    }

    @Transactional
    public void cleanUpNewsPosts() {
        Instant oldestDate = Instant.now().minus(removalDays, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        log.info("Deleting news posts older than {}.", oldestDate);
        List<NewsPost> oldPosts = newsPostManagementServiceBean.findNewsPostsUpdatedOlderThanAndLocked(oldestDate);
        newsPostManagementServiceBean.deleteNewsPosts(oldPosts);
    }

    public CompletableFuture<Message> sendNewsPost(String text, Message commandMessage) {
        NewsMessageModel model = NewsMessageModel
                .builder()
                .messageText(text)
                .message(commandMessage)
                .author(commandMessage.getMember())
                .build();
        log.info("Sending new message post based on message {}.", commandMessage.getIdLong());
        Long serverId = commandMessage.getGuild().getIdLong();
        MessageToSend messageToSend = templateService.renderEmbedTemplate(MESSAGE_TEMPLATE_KEY, model, serverId);
        List<CompletableFuture<Message>> messageFutures = postTargetService.sendEmbedInPostTarget(messageToSend, NewsPostTarget.NEWS_TARGET, serverId);
        return FutureUtils.toSingleFutureGeneric(messageFutures)
                .thenApply(unused -> {
                    Message createdMessage = messageFutures.get(0).join();
                    self.persistPost(commandMessage, createdMessage);
                    return createdMessage;
                });
    }

    public CompletableFuture<Void> updateNewsPostViaId(Long postId, String postText, Message updatedMessage) {
        NewsPost post = newsPostManagementServiceBean.getNewsPostForNewsMessageId(postId);
        if(post.isLocked()) {
            throw new NewsPostLockedException();
        }
        return updateNewsPostMessage(post, updatedMessage, postText);
    }

    public CompletableFuture<Void> updateNewsPost(NewsPost newsPost, Message updatedMessage) {
        String contentStripped = updatedMessage.getContentRaw();
        String command = contentStripped.split(" ")[0];
        String postText = updatedMessage.getContentRaw().replaceFirst(command, "");
        return updateNewsPostMessage(newsPost, updatedMessage, postText);
    }

    private CompletableFuture<Void> updateNewsPostMessage(NewsPost newsPost, Message updatedMessage, String postText) {
        NewsMessageModel model = NewsMessageModel
                .builder()
                .messageText(postText)
                .message(updatedMessage)
                .author(updatedMessage.getMember())
                .build();
        Long serverId = updatedMessage.getGuild().getIdLong();
        newsPost.setUpdated(Instant.now());
        log.info("Updating news post {} with new content based on message from user {} in server {}.",
                newsPost.getSourceMessageId(), updatedMessage.getIdLong(), updatedMessage.getGuild().getId());
        MessageToSend messageToSend = templateService.renderEmbedTemplate(MESSAGE_TEMPLATE_KEY, model, serverId);
        GuildMessageChannel newsChannel = channelService.getMessageChannelFromServer(serverId, newsPost.getNewsChannel().getId());
        return channelService.editMessageInAChannelFuture(messageToSend, newsChannel, newsPost.getNewsMessageId())
                .thenApply(message -> null);
    }

    @Transactional
    public void persistPost(Message commandMessage, Message createdMessage) {
        log.info("Persisting news post with created message {} based on command message {}.", createdMessage.getIdLong(), commandMessage.getIdLong());
        newsPostManagementServiceBean.createNewsPost(commandMessage, createdMessage);
    }

}
