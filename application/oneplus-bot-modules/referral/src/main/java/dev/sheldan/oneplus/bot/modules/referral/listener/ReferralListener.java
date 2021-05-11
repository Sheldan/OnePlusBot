package dev.sheldan.oneplus.bot.modules.referral.listener;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.listener.async.jda.AsyncMessageReceivedListener;
import dev.sheldan.abstracto.core.models.ServerUser;
import dev.sheldan.abstracto.core.models.database.PostTarget;
import dev.sheldan.abstracto.core.models.listener.MessageReceivedModel;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.MessageService;
import dev.sheldan.abstracto.core.service.management.PostTargetManagement;
import dev.sheldan.abstracto.core.utils.CompletableFutureList;
import dev.sheldan.oneplus.bot.modules.referral.config.ReferralFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.referral.config.ReferralPostTarget;
import dev.sheldan.oneplus.bot.modules.referral.model.template.Referral;
import dev.sheldan.oneplus.bot.modules.referral.model.template.ReferralPostModel;
import dev.sheldan.oneplus.bot.modules.referral.model.template.ReferralTooRecentModel;
import dev.sheldan.oneplus.bot.modules.referral.service.ReferralServiceBean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Slf4j
public class ReferralListener implements AsyncMessageReceivedListener {

    @Autowired
    private PostTargetManagement postTargetManagement;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ReferralServiceBean referralServiceBean;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ReferralListener self;

    @Qualifier("referralDelayExecutor")
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${oneplus.bot.referral.deleteDelaySeconds}")
    private Long deleteDelaySeconds;

    @Value("${oneplus.bot.referral.maxReferralCount}")
    private Long maxReferralLinksPerPost;

    private static final String NO_REFERRAL_LINK_FOUND_TEMPLATE_KEY = "referralListener_no_referral_link_found";
    private static final String REFERRAL_POST_TOO_RECENT_TEMPLATE_KEY = "referralListener_too_recent_post";
    private static final String REFERRAL_POST_EMBED_TEMPLATE_KEY = "referralListener_referral_post";

    @Override
    public DefaultListenerResult execute(MessageReceivedModel model) {
        Long serverId = model.getServerId();
        Optional<PostTarget> referralPostTargetOptional = postTargetManagement.getPostTargetOptional(ReferralPostTarget.REFERRAL, serverId);
        if(referralPostTargetOptional.isPresent()) {
            PostTarget referralTarget = referralPostTargetOptional.get();
            Message message = model.getMessage();
            if (message.getChannel().getIdLong() == referralTarget.getChannelReference().getId()) {
                Long authorId = message.getAuthor().getIdLong();
                if (!message.isFromGuild() || message.isWebhookMessage() || message.getType().isSystem()) {
                    log.info("Deleting illegal message by user {} in referral channel in server {}.", authorId, serverId);
                    messageService.deleteMessage(message).exceptionally(deletionErrorConsumer(message));
                    return DefaultListenerResult.IGNORED;
                }
                if(message.getAuthor().isBot()) {
                    log.debug("Ignoring message from a bot user {} in referral channel in server {}.", authorId, serverId);
                    return DefaultListenerResult.IGNORED;
                }
                List<Referral> foundReferrals = referralServiceBean.getReferralsFromMessage(message);
                if(foundReferrals.isEmpty()) {
                    log.info("Did not find referrals in message {} by user {} in server {} - deleting message.",
                            message.getIdLong(), authorId, serverId);
                    deleteAndNotify(message, NO_REFERRAL_LINK_FOUND_TEMPLATE_KEY, new Object());
                    return DefaultListenerResult.IGNORED;
                }

                log.info("Found {} referral links in message {} by user {} in server {}.",
                        foundReferrals.size(), message.getIdLong(), authorId, serverId);

                Instant nextReferralDate = referralServiceBean.getNextReferralDate(message.getMember());
                if(nextReferralDate.isAfter(Instant.now())) {
                    log.info("Referrals in message {} by user {} in server {} was before allowed repost date {} - deleting message.",
                            message.getIdLong(), authorId, serverId, nextReferralDate);
                    ReferralTooRecentModel templateModel = ReferralTooRecentModel
                            .builder()
                            .nextReferralDate(nextReferralDate)
                            .build();
                    deleteAndNotify(message, REFERRAL_POST_TOO_RECENT_TEMPLATE_KEY, templateModel);
                    return DefaultListenerResult.PROCESSED;
                }

                if(foundReferrals.size() > maxReferralLinksPerPost) {
                    log.info("More referral links ({}) than allowed ({}) in message {} in server {} by user {}.",
                            foundReferrals.size(), maxReferralLinksPerPost, message.getIdLong(), serverId, authorId);
                    foundReferrals = foundReferrals.subList(0, maxReferralLinksPerPost.intValue());
                }

                ReferralPostModel postModel = ReferralPostModel
                        .builder()
                        .referrals(foundReferrals)
                        .postingMember(message.getMember())
                        .build();
                ServerUser serverUser = ServerUser.fromMember(message.getMember());
                CompletableFutureList<Message> sendFutures = new CompletableFutureList<>(channelService
                        .sendEmbedTemplateInMessageChannelList(REFERRAL_POST_EMBED_TEMPLATE_KEY, postModel, message.getChannel()));
                CompletableFuture<Void> deletionFuture = messageService.deleteMessage(message);
                CompletableFuture.allOf(sendFutures.getMainFuture(), deletionFuture)
                        .thenAccept(unused -> self.updateReferralStateInDatabase(serverUser))
                .exceptionally(throwable -> {
                    log.error("Failed to delete or persist referral message from user {} in server {}.", authorId, serverId, throwable);
                    return null;
                });
            }
        }
        return DefaultListenerResult.IGNORED;
    }

    @Transactional
    public void updateReferralStateInDatabase(ServerUser serverUser) {
        referralServiceBean.updateDbState(serverUser);
    }

    private void deleteAndNotify(Message message, String usedTemplate, Object usedModel) {
        CompletableFutureList<Message> futures = new CompletableFutureList<>(channelService
                .sendEmbedTemplateInMessageChannelList(usedTemplate, usedModel, message.getChannel()));
        futures.getMainFuture().thenAccept(unused ->
                scheduledExecutorService.schedule(() ->
                                futures.getObjects().forEach(createdMessage -> messageService.deleteMessage(createdMessage)),
                deleteDelaySeconds, TimeUnit.SECONDS));
        futures.getMainFuture().exceptionally(throwable -> {
            log.error("Failed to send denial about setups with template {} message {} in channel {} in server {} by {}.",
                    usedTemplate, message.getIdLong(), message.getChannel().getIdLong(), message.getGuild().getIdLong(), message.getAuthor().getIdLong());
            return null;
        });
        messageService.deleteMessage(message).exceptionally(deletionErrorConsumer(message));
    }

    private Function<Throwable, Void> deletionErrorConsumer(Message message) {
        return throwable -> {
            log.error("Failed to delete setups message {} in channel {} in server {} by {}.",
                    message.getIdLong(), message.getChannel().getIdLong(), message.getGuild().getIdLong(), message.getAuthor().getIdLong());
            return null;
        };
    }

    @Override
    public FeatureDefinition getFeature() {
        return ReferralFeatureDefinition.REFERRAL;
    }
}
