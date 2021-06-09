package dev.sheldan.oneplus.bot.modules.faq.models.command.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaqCommandResponseEmbedColorConfig {
    @Builder.Default
    private Integer red = 0;
    @Builder.Default
    private Integer green = 0;
    @Builder.Default
    private Integer blue = 0;
}
