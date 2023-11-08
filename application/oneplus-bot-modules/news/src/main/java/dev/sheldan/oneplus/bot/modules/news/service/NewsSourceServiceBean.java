package dev.sheldan.oneplus.bot.modules.news.service;

import dev.sheldan.abstracto.core.service.PostTargetService;
import dev.sheldan.abstracto.core.templating.model.MessageToSend;
import dev.sheldan.abstracto.core.templating.service.TemplateService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.oneplus.bot.modules.news.config.NewsPostTarget;
import dev.sheldan.oneplus.bot.modules.news.model.ForumPostNotificationEntry;
import dev.sheldan.oneplus.bot.modules.news.model.ForumPostNotificationModel;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsForumPost;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsSource;
import dev.sheldan.oneplus.bot.modules.news.model.forum.ForumPost;
import dev.sheldan.oneplus.bot.modules.news.service.management.NewsForumPostManagementServiceBean;
import dev.sheldan.oneplus.bot.modules.news.service.management.NewsSourceManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.sheldan.oneplus.bot.modules.news.config.NewsFeature.NEWS_FORUM_POST_NOTIFICATION_SERVER_ID_ENV_NAME;

@Component
@Slf4j
public class NewsSourceServiceBean {

    @Autowired
    private ForumApiClient forumApiClient;

    @Autowired
    private NewsSourceManagementServiceBean newsSourceManagementServiceBean;

    @Autowired
    private NewsForumPostManagementServiceBean newsForumPostManagementServiceBean;

    @Autowired
    private PostTargetService postTargetService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private NewsSourceServiceBean self;

    private static final String NEWS_FORUM_POST_NOTIFICATION_TEMPLATE_KEY = "newsForumPost_notification";

    public void checkForNewThreads() {
        Long targetServerId = Long.parseLong(System.getenv(NEWS_FORUM_POST_NOTIFICATION_SERVER_ID_ENV_NAME));
        List<ForumPost> newForumPosts = getNewForumPosts();
        log.info("Found {} new forum posts.", newForumPosts.size());
        if(newForumPosts.isEmpty()) {
            return;
        }
        List<ForumPostNotificationEntry> entries = new ArrayList<>();
        newForumPosts.forEach(forumPost -> entries.add(ForumPostNotificationEntry.fromPost(forumPost)));

        ForumPostNotificationModel model = ForumPostNotificationModel
                .builder()
                .entries(entries)
                .build();

        MessageToSend messageToSend = templateService.renderEmbedTemplate(NEWS_FORUM_POST_NOTIFICATION_TEMPLATE_KEY, model, targetServerId);

        FutureUtils.toSingleFutureGeneric(postTargetService.sendEmbedInPostTarget(messageToSend, NewsPostTarget.FORUM_POST_NOTIFICATION, targetServerId))
                .thenAccept(unused -> {
                    log.info("Sent news forum post notification.");
                    self.persistForumPostsAndThreadCount(entries);
                }).exceptionally(throwable -> {
                    log.error("Failed to send news forum post notification.", throwable);
                    return null;
                });
    }

    @Transactional
    public void persistForumPostsAndThreadCount(List<ForumPostNotificationEntry> entries) {
        Map<Long, NewsSource> sourceMap = newsSourceManagementServiceBean.loadNewsSources()
                .stream()
                .collect(Collectors.toMap(NewsSource::getUserId, Function.identity()));

        entries.forEach(forumPostNotificationEntry ->
                newsForumPostManagementServiceBean.createPost(sourceMap.get(forumPostNotificationEntry.getCreatorId()), forumPostNotificationEntry.getPostId()));

        sourceMap.values().forEach(newsSource -> {
            Long currentThreadCount = forumApiClient.getCurrentThreadCount(newsSource);
            newsSource.setThreadCount(currentThreadCount);
        });
    }

    private boolean hasThreadCountChanged(NewsSource newsSource) {
        Long currentThreadCount = forumApiClient.getCurrentThreadCount(newsSource);
        return !currentThreadCount.equals(newsSource.getThreadCount());
    }

    private List<ForumPost> getNewForumPosts() {
        List<NewsSource> newsSources = newsSourceManagementServiceBean.loadNewsSources();
        log.info("Total news source count: {}", newsSources.size());
        List<NewsSource> sourcesWithChangedThreadCount = newsSources
                .stream()
                .filter(this::hasThreadCountChanged)
                .toList();

        log.info("News sources with new thread count: {}", sourcesWithChangedThreadCount.size());

        List<ForumPost> currentForumPosts = sourcesWithChangedThreadCount
                .stream()
                .map(newsSource -> forumApiClient.getPostsOfSource(newsSource))
                .flatMap(Collection::stream)
                .toList();

        log.info("Total amount of incoming forum posts: {}", currentForumPosts.size());
        if(currentForumPosts.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> incomingForumPostIds = currentForumPosts
                .stream()
                .map(ForumPost::getId)
                .collect(Collectors.toSet());

        List<NewsForumPost> existingNewsForumPosts = newsForumPostManagementServiceBean.getAllPosts();

        log.info("Total amount of existing and tracked forum posts: {}", existingNewsForumPosts.size());

        Set<Long> existingForumPostIds = existingNewsForumPosts
                .stream()
                .map(NewsForumPost::getId)
                .collect(Collectors.toSet());

        incomingForumPostIds.removeAll(existingForumPostIds);

        if(incomingForumPostIds.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, ForumPost> incomingPostMap = currentForumPosts
                .stream()
                .collect(Collectors.toMap(ForumPost::getId, Function.identity()));

        return incomingForumPostIds.stream().map(incomingPostMap::get).toList();
    }
}
