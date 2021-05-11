package dev.sheldan.oneplus.bot.modules.setup.service;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SetupServiceBean {

    private static final Pattern URL_REGEX = Pattern.compile("((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?");

    public boolean mightContainEmbed(Message message) {
        Matcher urlMatcher = URL_REGEX.matcher(message.getContentRaw());
        return urlMatcher.find();
    }

    public boolean currentlyValid(Message message) {
        return !message.getAttachments().isEmpty() ||
                (
                        !message.getEmbeds().isEmpty() &&
                        message
                            .getEmbeds()
                            .stream()
                        .anyMatch(messageEmbed -> messageEmbed.getType().equals(EmbedType.IMAGE))
                );
    }

    public boolean currentlyInvalid(Message message) {
        return !currentlyValid(message);
    }

}
