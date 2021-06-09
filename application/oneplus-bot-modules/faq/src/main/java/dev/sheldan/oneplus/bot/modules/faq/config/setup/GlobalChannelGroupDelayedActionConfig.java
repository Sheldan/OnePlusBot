package dev.sheldan.oneplus.bot.modules.faq.config.setup;

import dev.sheldan.abstracto.core.interactive.AutoDelayedAction;
import dev.sheldan.abstracto.core.interactive.DelayedActionConfig;
import dev.sheldan.abstracto.core.models.AServerChannelUserId;
import org.springframework.stereotype.Component;

@Component
public class GlobalChannelGroupDelayedActionConfig implements AutoDelayedAction {
    @Override
    public DelayedActionConfig getDelayedActionConfig(AServerChannelUserId aServerChannelUserId) {
        return GlobalChannelGroupDelayActionConfig
                .builder()
                .serverId(aServerChannelUserId.getGuildId())
                .build();
    }
}
