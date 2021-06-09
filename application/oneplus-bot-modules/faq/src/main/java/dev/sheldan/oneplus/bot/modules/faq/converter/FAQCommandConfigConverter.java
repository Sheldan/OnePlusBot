package dev.sheldan.oneplus.bot.modules.faq.converter;

import com.google.gson.Gson;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.*;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FAQCommandConfigConverter {

    @Autowired
    private Gson gson;

    public String serializeCommands(List<FAQCommand> asList) {
        List<FaqCommandConfig> config = asList.stream().map(faqCommand -> {
            List<FaqCommandResponseConfig> commandResponseConfigs = convertGroupCommands(faqCommand.getGroupResponses());
            List<String> aliases;
            if(faqCommand.getAliases() != null) {
                aliases = faqCommand.getAliases().stream().map(faqCommandAlias -> faqCommandAlias.getId().getAlias()).collect(Collectors.toList());
            } else {
                aliases = new ArrayList<>();
            }
            return FaqCommandConfig
                    .builder()
                    .faqCommandName(faqCommand.getName())
                    .global(faqCommand.getGlobal())
                    .responses(commandResponseConfigs)
                    .aliases(aliases)
                    .build();
        }).collect(Collectors.toList());

        return gson.toJson(config);
    }

    private List<FaqCommandResponseConfig> convertGroupCommands(List<FAQChannelGroupCommand> responses) {
        return responses.stream().map(faqChannelGroupCommand -> {
            List<FaqCommandResponseMessageConfig> responseConfigs = convertCommandResponses(faqChannelGroupCommand.getResponses());
            return FaqCommandResponseConfig
                    .builder()
                    .channelGroupName(faqChannelGroupCommand.getChannelGroup().getChannelGroup().getGroupName())
                    .messages(responseConfigs)
                    .build();
        }).collect(Collectors.toList());
    }

    private List<FaqCommandResponseMessageConfig> convertCommandResponses(List<FAQCommandResponse> responses) {
        return responses.stream().map(this::convertCommandResponse).collect(Collectors.toList());
    }

    private FaqCommandResponseMessageConfig convertCommandResponse(FAQCommandResponse response) {
        FaqCommandResponseEmbedConfig embedConfig = null;
        if(response.getDescription() != null) {
            FaqCommandResponseEmbedColorConfig colorConfig = FaqCommandResponseEmbedColorConfig
                    .builder()
                    .red(response.getRed())
                    .green(response.getGreen())
                    .blue(response.getBlue())
                    .build();
            FaqCommandResponseEmbedAuthorConfig authorConfig = FaqCommandResponseEmbedAuthorConfig
                    .builder()
                    .userId(response.getAuthorUserId())
                    .useBot(null)
                    .build();
            embedConfig = FaqCommandResponseEmbedConfig
                    .builder()
                    .description(response.getDescription())
                    .imageUrl(response.getImageURL())
                    .color(colorConfig)
                    .author(authorConfig)
                    .build();
        }
        return FaqCommandResponseMessageConfig
                .builder()
                .position(response.getId().getPosition())
                .additionalMessage(response.getAdditionalMessage())
                .embed(embedConfig)
                .build();
    }

}
