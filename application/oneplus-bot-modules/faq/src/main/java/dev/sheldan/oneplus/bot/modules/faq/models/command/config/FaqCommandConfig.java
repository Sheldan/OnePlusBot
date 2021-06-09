package dev.sheldan.oneplus.bot.modules.faq.models.command.config;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaqCommandConfig {
    private String faqCommandName;
    private Boolean global;
    private List<String> aliases;
    private List<FaqCommandResponseConfig> responses;
}
