package dev.sheldan.oneplus.bot.custom.moderation.model.template;

import dev.sheldan.abstracto.core.models.template.display.ChannelDisplay;
import dev.sheldan.abstracto.core.models.template.display.MemberDisplay;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WarningThresholdNotificationModel {
    private MemberDisplay memberDisplay;
    private Integer warnCount;
    private ChannelDisplay channelDisplay;
    private Long messageId;
}
