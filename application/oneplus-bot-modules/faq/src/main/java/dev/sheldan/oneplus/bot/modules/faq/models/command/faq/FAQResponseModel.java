package dev.sheldan.oneplus.bot.modules.faq.models.command.faq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class FAQResponseModel {
    @Builder.Default
    private List<FAQResponseMessageModel> messages = new ArrayList<>();
    @Builder.Default
    private List<String> availableCommands = new ArrayList<>();
}
