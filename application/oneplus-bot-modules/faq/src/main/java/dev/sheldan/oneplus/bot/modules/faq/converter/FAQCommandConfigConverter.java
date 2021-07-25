package dev.sheldan.oneplus.bot.modules.faq.converter;

import com.google.gson.Gson;
import dev.sheldan.abstracto.core.service.BotService;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.*;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FAQCommandConfigConverter {

    @Autowired
    private Gson gson;

    @Autowired
    private BotService botService;

    public String serializeCommands(List<FAQCommand> asList) {
        Long botId = botService.getInstance().getSelfUser().getIdLong();
        asList.sort(Comparator.comparing(FAQCommand::getName));
        List<FaqCommandConfig> config = asList.stream().map(faqCommand -> {
            List<FAQChannelGroupCommand> responses = faqCommand.getGroupResponses();
            responses.sort(Comparator.comparing(groupCommand -> groupCommand.getChannelGroup().getChannelGroup().getGroupName()));
            List<FaqCommandResponseConfig> commandResponseConfigs = convertGroupCommands(responses, botId);
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

    private List<FaqCommandResponseConfig> convertGroupCommands(List<FAQChannelGroupCommand> responses, Long botUserId) {
        return responses.stream().map(faqChannelGroupCommand -> {
            List<FaqCommandResponseMessageConfig> responseConfigs = convertCommandResponses(faqChannelGroupCommand.getResponses(), botUserId);
            return FaqCommandResponseConfig
                    .builder()
                    .channelGroupName(faqChannelGroupCommand.getChannelGroup().getChannelGroup().getGroupName())
                    .messages(responseConfigs)
                    .build();
        }).collect(Collectors.toList());
    }

    private List<FaqCommandResponseMessageConfig> convertCommandResponses(List<FAQCommandResponse> responses, Long botUserId) {
        return responses.stream().map(response -> convertCommandResponse(response, botUserId)).collect(Collectors.toList());
    }

    private FaqCommandResponseMessageConfig convertCommandResponse(FAQCommandResponse response, Long botUserId) {
        FaqCommandResponseEmbedConfig embedConfig = null;
        if(response.getDescription() != null) {
            boolean useBot = response.getAuthorUserId().equals(botUserId);
            FaqCommandResponseEmbedColorConfig colorConfig = FaqCommandResponseEmbedColorConfig
                    .builder()
                    .red(response.getRed())
                    .green(response.getGreen())
                    .blue(response.getBlue())
                    .build();
            FaqCommandResponseEmbedAuthorConfig authorConfig = FaqCommandResponseEmbedAuthorConfig
                    .builder()
                    .userId(useBot ? null : response.getAuthorUserId())
                    .useBot(useBot)
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
