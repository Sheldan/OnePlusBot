package dev.sheldan.oneplus.bot.modules.faq.models.command.list;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ListFAQCommandsCommandModel {
    private String commandName;
    private List<String> aliases;
    private List<ListFAQCommandsCommandChannelGroupModel> channelGroups;
}
