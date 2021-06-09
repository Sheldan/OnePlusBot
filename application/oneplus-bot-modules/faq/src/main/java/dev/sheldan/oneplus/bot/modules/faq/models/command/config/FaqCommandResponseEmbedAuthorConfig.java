package dev.sheldan.oneplus.bot.modules.faq.models.command.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaqCommandResponseEmbedAuthorConfig {
    private Long userId;
    private Boolean useBot;
}
