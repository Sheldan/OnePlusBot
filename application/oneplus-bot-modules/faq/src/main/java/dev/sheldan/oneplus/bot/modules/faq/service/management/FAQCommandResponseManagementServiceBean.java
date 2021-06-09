package dev.sheldan.oneplus.bot.modules.faq.service.management;

import dev.sheldan.oneplus.bot.modules.faq.models.command.config.FaqCommandResponseEmbedColorConfig;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.FaqCommandResponseEmbedConfig;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.FaqCommandResponseMessageConfig;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.CommandResponseId;
import dev.sheldan.oneplus.bot.modules.faq.repository.FAQCommandResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class FAQCommandResponseManagementServiceBean {

    @Autowired
    private FAQCommandResponseRepository responseRepository;

    public FAQCommandResponse createResponse(FaqCommandResponseMessageConfig config, int position, FAQChannelGroupCommand groupCommand) {
        CommandResponseId id = CommandResponseId
                .builder()
                .commandId(groupCommand.getCommand().getId())
                .channelGroupId(groupCommand.getChannelGroup().getId())
                .position(position)
                .build();
        FAQCommandResponse.FAQCommandResponseBuilder builder = FAQCommandResponse
                .builder()
                .additionalMessage(config.getAdditionalMessage())
                .id(id);

        FaqCommandResponseEmbedConfig embedConfig = config.getEmbed();
        if(embedConfig != null) {
            log.debug("The response to create in command {} has an embed.", groupCommand.getCommand().getId());
            FaqCommandResponseEmbedColorConfig colorConfig = embedConfig.getColor();
            if(colorConfig != null) {
                builder = builder.blue(colorConfig.getBlue())
                        .red(colorConfig.getRed())
                        .green(colorConfig.getGreen());
            }
            if(embedConfig.getAuthor() != null) {
                builder = builder.authorUserId(embedConfig.getAuthor().getUserId());
            }
            builder = builder.description(embedConfig.getDescription())
                    .imageURL(embedConfig.getImageUrl());
        }
        log.info("Creating faq command response for command {} in server {}. There area already {} responses.",
                groupCommand.getCommand().getId(), groupCommand.getCommand().getServer().getId(), groupCommand.getResponses().size());
        FAQCommandResponse builtCommand = builder.build();
        builtCommand.setGroupCommand(groupCommand);
        groupCommand.getResponses().add(builtCommand);
        return responseRepository.save(builtCommand);
    }

    public void deleteCommandResponse(FAQCommandResponse response) {
        log.info("Deleting fq command response for command {} in channel group {}.",
                response.getGroupCommand().getCommand().getId(), response.getGroupCommand().getChannelGroup().getId());
        responseRepository.delete(response);
    }

    public void deleteCommandResponses(List<FAQCommandResponse> responseList) {
        log.info("Deleting {} responses.", responseList.size());
        responseRepository.deleteAll(responseList);
    }
}
