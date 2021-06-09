package dev.sheldan.oneplus.bot.modules.faq.models.command.config;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaqCommandResponseConfig {
    private String channelGroupName;
    private List<FaqCommandResponseMessageConfig> messages;
}
