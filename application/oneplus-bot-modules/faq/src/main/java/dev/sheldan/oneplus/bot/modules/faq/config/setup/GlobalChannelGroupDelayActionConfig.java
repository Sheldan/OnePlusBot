package dev.sheldan.oneplus.bot.modules.faq.config.setup;

import dev.sheldan.abstracto.core.interactive.DelayedActionConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GlobalChannelGroupDelayActionConfig implements DelayedActionConfig {

    private Long serverId;

    @Override
    public String getTemplateName() {
        return "setup_global_channel_group_info";
    }

    @Override
    public Object getTemplateModel() {
        return new Object();
    }
}
