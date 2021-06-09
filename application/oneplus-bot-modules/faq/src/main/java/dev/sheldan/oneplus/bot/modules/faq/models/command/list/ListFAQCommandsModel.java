package dev.sheldan.oneplus.bot.modules.faq.models.command.list;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Builder
@Getter
public class ListFAQCommandsModel {
    private List<ListFAQCommandsCommandModel> commands;
}


