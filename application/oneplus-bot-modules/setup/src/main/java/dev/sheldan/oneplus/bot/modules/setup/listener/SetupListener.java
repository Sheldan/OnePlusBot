package dev.sheldan.oneplus.bot.modules.setup.listener;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.listener.async.jda.AsyncMessageReceivedListener;
import dev.sheldan.abstracto.core.models.database.PostTarget;
import dev.sheldan.abstracto.core.models.listener.MessageReceivedModel;
import dev.sheldan.abstracto.core.service.MessageService;
import dev.sheldan.abstracto.core.service.ReactionService;
import dev.sheldan.abstracto.core.service.management.PostTargetManagement;
import dev.sheldan.oneplus.bot.modules.setup.config.SetupFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.setup.config.SetupPostTarget;
import dev.sheldan.oneplus.bot.modules.setup.service.SetupServiceBean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SetupListener implements AsyncMessageReceivedListener {

    @Autowired
    private PostTargetManagement postTargetManagement;

    @Autowired
    private SetupServiceBean setupServiceBean;

    @Qualifier("setupDelayedExecutor")
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${oneplus.bot.setup.deletionDelaySeconds}")
    private Long deletionDelay;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ReactionService reactionService;

    public static final String SETUP_UPVOTE_EMOTE_KEY = "setupUpvote";

    @Override
    public DefaultListenerResult execute(MessageReceivedModel model) {
        Long serverId = model.getServerId();
        Optional<PostTarget> setupPostTargetOptional = postTargetManagement.getPostTargetOptional(SetupPostTarget.SETUP, serverId);
        if(setupPostTargetOptional.isPresent()) {
            PostTarget setupTarget = setupPostTargetOptional.get();
            Message originalMessage = model.getMessage();
            if(originalMessage.getChannel().getIdLong() == setupTarget.getChannelReference().getId()) {
                if(!originalMessage.isFromGuild() || originalMessage.isWebhookMessage() || originalMessage.getType().isSystem()) {
                    messageService.deleteMessage(originalMessage);
                    return DefaultListenerResult.IGNORED;
                }
                boolean currentlyInvalid = setupServiceBean.currentlyInvalid(originalMessage);
                if(currentlyInvalid) {
                    if(setupServiceBean.mightContainEmbed(originalMessage)) {
                        log.info("Setup message did not contain embeds not attachments, but a link - waiting for embeds on message {}" +
                                " in channel {} in guild {} by user {}.", originalMessage.getIdLong(), originalMessage.getChannel().getIdLong(),
                                originalMessage.getGuild().getIdLong(), originalMessage.getAuthor().getIdLong());
                        scheduledExecutorService.schedule(() -> {
                            messageService.loadMessage(originalMessage).thenAccept(loadedMessage -> {
                                if(setupServiceBean.currentlyInvalid(loadedMessage)) {
                                    log.info("Message did not contain attachments nor embeds after a delay - deleting setup message {}.", loadedMessage.getIdLong());
                                    messageService.deleteMessage(loadedMessage);
                                } else {
                                    log.info("Message contained embeds/attachments after a delay - message was accepted {}.", loadedMessage.getIdLong());
                                    reactionService.addReactionToMessage(SETUP_UPVOTE_EMOTE_KEY, serverId, loadedMessage);
                                }
                            });
                        }, deletionDelay, TimeUnit.SECONDS);
                    } else {
                        log.info("Did not find any attachments nor embeds and no link to lead to any embeds - deleting setup message {} in channel {} in server {}" +
                                        "by user {} for setup.", originalMessage.getIdLong(), originalMessage.getChannel().getIdLong(), originalMessage.getGuild().getIdLong(),
                                originalMessage.getAuthor().getIdLong());
                        messageService.deleteMessage(originalMessage);
                    }
                } else {
                    log.info("Accepting setup message {} in channel {} in guild {} from user {}.", originalMessage.getIdLong(),
                            originalMessage.getChannel().getIdLong(), originalMessage.getGuild().getIdLong(), originalMessage.getAuthor().getIdLong());
                    reactionService.addReactionToMessage(SETUP_UPVOTE_EMOTE_KEY, serverId, originalMessage);
                }
            }
        }
        return DefaultListenerResult.IGNORED;
    }

    @Override
    public FeatureDefinition getFeature() {
        return SetupFeatureDefinition.SETUP;
    }
}
