package dev.sheldan.oneplus.bot.custom.moderation.listener;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.models.ServerUser;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.models.template.display.ChannelDisplay;
import dev.sheldan.abstracto.core.models.template.display.MemberDisplay;
import dev.sheldan.abstracto.core.service.*;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import dev.sheldan.abstracto.core.templating.model.MessageToSend;
import dev.sheldan.abstracto.core.templating.service.TemplateService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.abstracto.moderation.listener.WarningCreatedListener;
import dev.sheldan.abstracto.moderation.model.listener.WarningCreatedEventModel;
import dev.sheldan.abstracto.moderation.service.management.WarnManagementService;
import dev.sheldan.oneplus.bot.custom.moderation.config.ModerationCustomFeature;
import dev.sheldan.oneplus.bot.custom.moderation.config.ModerationCustomFeatureDefinition;
import dev.sheldan.oneplus.bot.custom.moderation.config.ModerationCustomPostTarget;
import dev.sheldan.oneplus.bot.custom.moderation.model.template.WarningThresholdNotificationModel;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WarningAddedListener implements WarningCreatedListener {

    @Autowired
    private ConfigService configService;

    @Autowired
    private WarnManagementService warnManagementService;

    @Autowired
    private UserInServerManagementService userInServerManagementService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PostTargetService postTargetService;

    @Autowired
    private TemplateService templateService;

    public static final String WARN_THRESHOLD_NOTIFICATION_TEMPLATE_KEY = "warning_threshold_notification";

    @Override
    public DefaultListenerResult execute(WarningCreatedEventModel model) {
        Long warnNotificationAmount = configService.getLongValueOrConfigDefault(ModerationCustomFeature.WARN_NOTIFICATION_THRESHOLD, model.getServerId());
        ServerUser warnedUser = ServerUser
                .builder()
                .userId(model.getWarnedUserId())
                .serverId(model.getServerId())
                .build();
        AUserInAServer warnedUserInAServer = userInServerManagementService.loadOrCreateUser(warnedUser);
        List<Long> activeWarnsForUser = warnManagementService.getActiveWarnsForUser(warnedUserInAServer)
                .stream().map(warning -> warning.getWarnId().getId()).collect(Collectors.toList());
        Set<Long> warnIds = new HashSet<>(activeWarnsForUser);
        // we cant be sure we receive the newly persisted warning yet, sadly
        warnIds.add(model.getWarningId());
        if(warnIds.size() == warnNotificationAmount) {
            Long serverId = model.getServerId();

            Long channelId = model.getWarningChannelId();
            Long warnedUserId = model.getWarnedUserId();
            GuildMessageChannel channel = channelService.getMessageChannelFromServer(serverId, channelId);
            WarningThresholdNotificationModel notificationModel = WarningThresholdNotificationModel
                    .builder()
                    .channelDisplay(ChannelDisplay.fromChannel(channel))
                    .memberDisplay(MemberDisplay.fromAUserInAServer(warnedUserInAServer))
                    .messageId(model.getWarningMessageId())
                    .warnCount(warnIds.size())
                    .build();

            MessageToSend messageToSend = templateService.renderEmbedTemplate(WARN_THRESHOLD_NOTIFICATION_TEMPLATE_KEY, notificationModel,  serverId);
            FutureUtils.toSingleFutureGeneric(postTargetService.sendEmbedInPostTarget(messageToSend, ModerationCustomPostTarget.WARN_THRESHOLD_NOTIFICATION, serverId))
            .thenAccept(unused -> log.info("Warn threshold notification sent for user {} in server {}.", warnedUserId, serverId))
            .exceptionally(throwable -> {
                log.error("Failed to sent warn threshold notification for user {} in server {}.", warnedUserId, serverId, throwable);
                return null;
            });
        }
        return DefaultListenerResult.PROCESSED;
    }

    @Override
    public FeatureDefinition getFeature() {
        return ModerationCustomFeatureDefinition.MODERATION_CUSTOM;
    }
}
