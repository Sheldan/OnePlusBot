package dev.sheldan.oneplus.bot.modules.faq.models.command.faquses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FAQCommandResponseUsage {
    private String channelGroupName;
    private Integer uses;
}
