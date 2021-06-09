package dev.sheldan.oneplus.bot.modules.faq.models.command.list;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListFAQCommandsCommandChannelGroupModel {
    private String channelGroupName;
    private Integer responseCount;
}
