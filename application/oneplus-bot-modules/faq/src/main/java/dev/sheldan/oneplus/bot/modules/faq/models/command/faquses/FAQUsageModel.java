package dev.sheldan.oneplus.bot.modules.faq.models.command.faquses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FAQUsageModel {
    private List<FAQCommandUsage> uses;
}
