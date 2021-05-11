package dev.sheldan.oneplus.bot.modules.setups.listener;

import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.listener.async.jda.AsyncMessageReceivedListener;
import dev.sheldan.abstracto.core.models.database.PostTarget;
import dev.sheldan.abstracto.core.models.listener.MessageReceivedModel;
import dev.sheldan.abstracto.core.service.MessageService;
import dev.sheldan.abstracto.core.service.ReactionService;
import dev.sheldan.abstracto.core.service.management.PostTargetManagement;
import dev.sheldan.oneplus.bot.modules.setups.config.SetupsFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.setups.config.SetupsPostTarget;
import dev.sheldan.oneplus.bot.modules.setups.service.SetupsService;
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
public class SetupsListener implements AsyncMessageReceivedListener {

    @Autowired
    private PostTargetManagement postTargetManagement;

    @Autowired
    private SetupsService setupsService;

    @Qualifier("setupsDelayedExecutor")
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${abstracto.setups.deletionDelaySeconds}")
    private Long deletionDelay;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ReactionService reactionService;

    public static final String SETUPS_UPVOTE_EMOTE_KEY = "setupsUpvote";

    @Override
    public DefaultListenerResult execute(MessageReceivedModel model) {
        Optional<PostTarget> setupsPostTargetOptional = postTargetManagement.getPostTargetOptional(SetupsPostTarget.SETUPS, model.getServerId());
        if(setupsPostTargetOptional.isPresent()) {
            PostTarget setupsTarget = setupsPostTargetOptional.get();
            Message originalMessage = model.getMessage();
            if(originalMessage.getChannel().getIdLong() == setupsTarget.getChannelReference().getId()) {
                Long serverId = model.getServerId();
                boolean currentlyInvalid = setupsService.currentlyInvalid(originalMessage);
                if(currentlyInvalid) {
                    if(setupsService.mightContainEmbed(originalMessage)) {
                        log.info("Setup message did not contain embeds not attachments, but a link - waiting for embeds on message {}" +
                                " in channel {} in guild {} by user {}.", originalMessage.getIdLong(), originalMessage.getChannel().getIdLong(),
                                originalMessage.getGuild().getIdLong(), originalMessage.getAuthor().getIdLong());
                        scheduledExecutorService.schedule(() -> {
                            messageService.loadMessage(originalMessage).thenAccept(loadedMessage -> {
                                if(setupsService.currentlyInvalid(loadedMessage)) {
                                    log.info("Message did not contain attachments nor embeds after a delay - deleting setups message {}.", loadedMessage.getIdLong());
                                    messageService.deleteMessage(loadedMessage);
                                } else {
                                    log.info("Message contained embeds/attachments after a delay - message was accepted {}.", loadedMessage.getIdLong());
                                    reactionService.addReactionToMessage(SETUPS_UPVOTE_EMOTE_KEY, serverId, loadedMessage);
                                }
                            });
                        }, deletionDelay, TimeUnit.SECONDS);
                    } else {
                        log.info("Did not find any attachments nor embeds and no link to lead to any embeds - deleting setup message {} in channel {} in server {}" +
                                        "by user {} for setups.", originalMessage.getIdLong(), originalMessage.getChannel().getIdLong(), originalMessage.getGuild().getIdLong(),
                                originalMessage.getAuthor().getIdLong());
                        messageService.deleteMessage(originalMessage);
                    }
                } else {
                    log.info("Accepting setups message {} in channel {} in guild {} from user {}.", originalMessage.getIdLong(),
                            originalMessage.getChannel().getIdLong(), originalMessage.getGuild().getIdLong(), originalMessage.getAuthor().getIdLong());
                    reactionService.addReactionToMessage(SETUPS_UPVOTE_EMOTE_KEY, serverId, originalMessage);
                }
            }
        }
        return DefaultListenerResult.IGNORED;
    }

    @Override
    public FeatureDefinition getFeature() {
        return SetupsFeatureDefinition.SETUPS;
    }
}
