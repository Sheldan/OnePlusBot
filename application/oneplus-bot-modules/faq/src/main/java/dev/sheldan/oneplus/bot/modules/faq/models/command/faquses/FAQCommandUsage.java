package dev.sheldan.oneplus.bot.modules.faq.models.command.faquses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FAQCommandUsage {
    private String faqCommandName;
    private List<FAQCommandResponseUsage> faqChannelGroupUsages;
}
