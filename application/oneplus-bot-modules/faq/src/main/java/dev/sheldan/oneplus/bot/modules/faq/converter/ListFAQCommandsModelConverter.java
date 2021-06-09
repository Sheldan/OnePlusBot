package dev.sheldan.oneplus.bot.modules.faq.converter;

import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.models.command.list.ListFAQCommandsCommandChannelGroupModel;
import dev.sheldan.oneplus.bot.modules.faq.models.command.list.ListFAQCommandsModel;
import dev.sheldan.oneplus.bot.modules.faq.models.command.list.ListFAQCommandsCommandModel;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListFAQCommandsModelConverter {

    public ListFAQCommandsModel fromFAQCommands(List<FAQCommand> faqCommandList) {
        List<ListFAQCommandsCommandModel> commands = faqCommandList
                .stream()
                .map(this::convertCommand)
                .collect(Collectors.toList());
        return ListFAQCommandsModel
                .builder()
                .commands(commands)
                .build();
    }

    private ListFAQCommandsCommandModel convertCommand(FAQCommand faqCommand) {
        List<ListFAQCommandsCommandChannelGroupModel> channelGroups = faqCommand
                .getGroupResponses()
                .stream()
                .map(this::convertGroupCommand)
                .collect(Collectors.toList());

        List<String> aliases;
        if(faqCommand.getAliases() != null) {
            aliases = faqCommand.getAliases().stream().map(faqCommandAlias -> faqCommandAlias.getId().getAlias()).collect(Collectors.toList());
        } else {
            aliases = new ArrayList<>();
        }
        return ListFAQCommandsCommandModel
                .builder()
                .commandName(faqCommand.getName())
                .aliases(aliases)
                .channelGroups(channelGroups)
                .build();
    }

    private ListFAQCommandsCommandChannelGroupModel convertGroupCommand(FAQChannelGroupCommand faqChannelGroupCommand) {
        AChannelGroup channelGroup = faqChannelGroupCommand.getChannelGroup().getChannelGroup();
        return ListFAQCommandsCommandChannelGroupModel
                .builder()
                .responseCount(faqChannelGroupCommand.getResponses().size())
                .channelGroupName(channelGroup.getGroupName())
                .build();
    }
}
