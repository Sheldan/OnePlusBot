package dev.sheldan.oneplus.bot.modules.faq.listener;

import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.listener.sync.entity.AsyncChannelGroupCreatedListener;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.abstracto.core.models.listener.ChannelGroupCreatedListenerModel;
import dev.sheldan.abstracto.core.service.management.ChannelGroupManagementService;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQServiceBean;
import dev.sheldan.oneplus.bot.modules.faq.service.management.FAQChannelGroupManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class FAQAsyncChannelGroupCreatedListener implements AsyncChannelGroupCreatedListener {

    @Autowired
    private ChannelGroupManagementService channelGroupManagementService;

    @Autowired
    private FAQChannelGroupManagementServiceBean faqChannelGroupManagementServiceBean;

    @Value("${abstracto.faq.globalChannelGroupName}")
    private String globalChannelGroupName;

    @Override
    public DefaultListenerResult execute(ChannelGroupCreatedListenerModel model) {
        AChannelGroup channelGroup = channelGroupManagementService.findChannelGroupById(model.getChannelGroupId());
        if(channelGroup.getChannelGroupType().getGroupTypeKey().equals(FAQServiceBean.FAQ_CHANNEL_GROUP_KEY)) {
            FAQChannelGroup createdChannelGroup = faqChannelGroupManagementServiceBean.createFAQChannelGroup(channelGroup);
            if(channelGroup.getGroupName().equals(globalChannelGroupName)) {
                Optional<FAQChannelGroup> globalGroup = faqChannelGroupManagementServiceBean.getGlobalChannelGroup(channelGroup.getServer());
                globalGroup.ifPresent(faqChannelGroup -> {
                    log.info("Setting channel group {} to not be global for server {}.", faqChannelGroup.getId(), channelGroup.getServer().getId());
                    faqChannelGroup.setGlobal(false);
                });
                log.info("Setting channel group {} to be global for server {}.", channelGroup.getId(), channelGroup.getServer().getId());
                createdChannelGroup.setGlobal(true);
            }
            return DefaultListenerResult.PROCESSED;
        }
        return DefaultListenerResult.IGNORED;
    }
}
