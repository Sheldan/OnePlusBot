package dev.sheldan.oneplus.bot.modules.faq.config.setup;

import dev.sheldan.abstracto.core.interactive.DelayedAction;
import dev.sheldan.abstracto.core.interactive.DelayedActionConfig;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.ChannelGroupType;
import dev.sheldan.abstracto.core.service.management.ChannelGroupManagementService;
import dev.sheldan.abstracto.core.service.management.ChannelGroupTypeManagementService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static dev.sheldan.oneplus.bot.modules.faq.service.FAQServiceBean.FAQ_CHANNEL_GROUP_KEY;

@Component
@Slf4j
public class GlobalChannelGroupSetupDelayedAction implements DelayedAction {

    @Autowired
    private ChannelGroupManagementService channelGroupManagementService;

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private ChannelGroupTypeManagementService channelGroupTypeManagementService;

    @Value("${abstracto.faq.globalChannelGroupName}")
    private String globalChannelGroupName;

    @Override
    public void execute(DelayedActionConfig delayedActionConfig) {
        GlobalChannelGroupDelayActionConfig config = (GlobalChannelGroupDelayActionConfig) delayedActionConfig;
        AServer server = serverManagementService.loadServer(config.getServerId());
        if(!channelGroupManagementService.doesChannelGroupExist(globalChannelGroupName, server)) {
            ChannelGroupType faqChannelGroupType = channelGroupTypeManagementService.findChannelGroupTypeByKey(FAQ_CHANNEL_GROUP_KEY);
            channelGroupManagementService.createChannelGroup(globalChannelGroupName, server, faqChannelGroupType);
        } else {
            log.info("Global channel group named {} already exists for server {}.", globalChannelGroupName, config.getServerId());
        }
    }

    @Override
    public boolean handles(DelayedActionConfig delayedActionConfig) {
        return delayedActionConfig instanceof GlobalChannelGroupDelayActionConfig;
    }
}
