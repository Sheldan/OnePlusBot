package dev.sheldan.oneplus.bot.modules.news.service.management;

import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import dev.sheldan.oneplus.bot.modules.news.exception.NewsPostNotFoundException;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsPost;
import dev.sheldan.oneplus.bot.modules.news.repository.NewsPostRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class NewsPostManagementServiceBean {

    @Autowired
    private NewsPostRepository newsPostRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserInServerManagementService userInServerManagementService;

    public NewsPost createNewsPost(Message commandMessage, Message createdMessage) {
        AChannel sourceChannel = entityManager.getReference(AChannel.class, commandMessage.getChannel().getIdLong());
        AChannel newsChannel = entityManager.getReference(AChannel.class, createdMessage.getChannel().getIdLong());
        AUserInAServer author = userInServerManagementService.loadOrCreateUser(commandMessage.getMember());
        NewsPost post = NewsPost
                .builder()
                .sourceChannel(sourceChannel)
                .newsChannel(newsChannel)
                .author(author)
                .newsMessageId(createdMessage.getIdLong())
                .sourceMessageId(commandMessage.getIdLong())
                .server(author.getServerReference())
                .locked(false)
                .build();
        log.debug("Created news post based on message {}.", createdMessage.getIdLong());
        return newsPostRepository.save(post);
    }

    public Optional<NewsPost> getNewsPostForSourceMessage(Long sourceMessageId) {
        return newsPostRepository.findById(sourceMessageId);
    }

    public Optional<NewsPost> getNewsPostForNewsMessageIdOptional(Long sourceMessageId) {
        return newsPostRepository.findByNewsMessageId(sourceMessageId);
    }

    public NewsPost getNewsPostForNewsMessageId(Long sourceMessageId) {
        return getNewsPostForNewsMessageIdOptional(sourceMessageId).orElseThrow(NewsPostNotFoundException::new);
    }

    public List<NewsPost> findNewsPostsOlderNotLocked(Instant pointInTime) {
        log.debug("Checking for not locked news posts older than {}", pointInTime);
        return newsPostRepository.findByCreatedLessThanAndLockedFalse(pointInTime);
    }

    public List<NewsPost> findNewsPostsUpdatedOlderThanAndLocked(Instant pointInTime) {
        log.debug("Checking for not locked news posts updated older than {}.", pointInTime);
        return newsPostRepository.findByUpdatedLessThanAndLockedTrue(pointInTime);
    }

    public void deleteNewsPosts(List<NewsPost> postsToDelete) {
        log.info("Deleting {} news posts.", postsToDelete.size());
        postsToDelete.forEach(newsPost -> log.info("Deleting news post {}",  newsPost.getSourceMessageId()));
        newsPostRepository.deleteAll(postsToDelete);
    }
}
