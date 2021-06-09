package dev.sheldan.oneplus.bot.modules.faq.models.command.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaqCommandResponseMessageConfig {
    private Integer position;
    private String additionalMessage;
    private FaqCommandResponseEmbedConfig embed;
}
