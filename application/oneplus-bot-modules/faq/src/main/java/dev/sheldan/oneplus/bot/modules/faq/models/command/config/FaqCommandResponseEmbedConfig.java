package dev.sheldan.oneplus.bot.modules.faq.models.command.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaqCommandResponseEmbedConfig {
    private String description;
    private String imageUrl;
    private FaqCommandResponseEmbedColorConfig color;
    private FaqCommandResponseEmbedAuthorConfig author;
}
