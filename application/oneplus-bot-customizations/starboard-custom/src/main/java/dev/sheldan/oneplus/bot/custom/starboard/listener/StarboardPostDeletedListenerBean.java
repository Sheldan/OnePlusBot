package dev.sheldan.oneplus.bot.custom.starboard.listener;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.service.MemberService;
import dev.sheldan.abstracto.core.service.MessageService;
import dev.sheldan.abstracto.core.service.PostTargetService;
import dev.sheldan.abstracto.core.templating.model.MessageToSend;
import dev.sheldan.abstracto.core.templating.service.TemplateService;
import dev.sheldan.abstracto.starboard.listener.StarboardPostDeletedListener;
import dev.sheldan.abstracto.starboard.model.StarboardPostDeletedModel;
import dev.sheldan.abstracto.starboard.service.management.StarboardPostManagementService;
import dev.sheldan.oneplus.bot.custom.starboard.config.StarboardCustomFeatureDefinition;
import dev.sheldan.oneplus.bot.custom.starboard.config.StarboardCustomPostTarget;
import dev.sheldan.oneplus.bot.custom.starboard.model.StarboardPostDeletedNotificationModel;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class StarboardPostDeletedListenerBean implements StarboardPostDeletedListener {

    @Autowired
    private PostTargetService postTargetService;

    @Autowired
    private StarboardPostManagementService starboardPostManagementService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StarboardPostDeletedListenerBean self;

    @Autowired
    private TemplateService templateService;

    private static final String STARBOARD_POST_DELETED_NOTIFICATION_TEMPLATE_KEY = "starboard_post_deleted_notification";

    @Override
    public DefaultListenerResult execute(StarboardPostDeletedModel model) {
        Long serverId = model.getServerId();
        CompletableFuture<Message> starboardMessageFuture = messageService.loadMessage(serverId, model.getStarboardMessage().getChannelId(), model.getStarboardMessage().getMessageId());
        CompletableFuture<Message> starredMessageFuture = messageService.loadMessage(serverId, model.getStarredMessage().getChannelId(), model.getStarredMessage().getMessageId());
        CompletableFuture<Member> starredUserFuture = memberService.retrieveMemberInServer(model.getStarredUser());
        CompletableFuture<Member> starringUserFuture = memberService.retrieveMemberInServer(model.getLastStarrer());
        List<CompletableFuture> futures = Arrays.asList(starboardMessageFuture, starredMessageFuture, starredUserFuture, starringUserFuture);
        CompletableFuture.allOf(starboardMessageFuture, starredMessageFuture, starredUserFuture, starringUserFuture)
                .whenComplete((unused, throwable) -> self.sendNotification(futures, model));
        log.info("Starboard deleted event for post {}.", model.getStarboardPostId());
        return DefaultListenerResult.PROCESSED;
    }

    @Transactional
    public void sendNotification(List<CompletableFuture> futures, StarboardPostDeletedModel model) {
        Message starboardMessage = futures.get(0).isCompletedExceptionally() ? null : (Message) futures.get(0).join();
        Message starredMessage = futures.get(1).isCompletedExceptionally() ? null : (Message) futures.get(1).join();
        Member starredMember = futures.get(2).isCompletedExceptionally() ? null : (Member) futures.get(2).join();
        Member starringMember = futures.get(3).isCompletedExceptionally() ? null : (Member) futures.get(3).join();
        StarboardPostDeletedNotificationModel templateModel = StarboardPostDeletedNotificationModel
                .builder()
                .starboardMessage(starboardMessage)
                .starredMessage(starredMessage)
                .starredMember(starredMember)
                .starringMember(starringMember)
                .starredUserId(model.getStarredUser().getUserId())
                .starringUserId(model.getLastStarrer().getUserId())
                .build();
        MessageToSend messageToSend = templateService.renderEmbedTemplate(STARBOARD_POST_DELETED_NOTIFICATION_TEMPLATE_KEY, templateModel);
        postTargetService.sendEmbedInPostTarget(messageToSend, StarboardCustomPostTarget.STARBOARD_NOTIFICATION, model.getServerId());
    }

    @Override
    public FeatureDefinition getFeature() {
        return StarboardCustomFeatureDefinition.STARBOARD_NOTIFICATION;
    }

}
