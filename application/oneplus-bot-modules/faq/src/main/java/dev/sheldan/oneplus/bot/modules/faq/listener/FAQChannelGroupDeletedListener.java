package dev.sheldan.oneplus.bot.modules.faq.listener;

import dev.sheldan.abstracto.core.listener.DefaultListenerResult;
import dev.sheldan.abstracto.core.listener.sync.entity.ChannelGroupDeletedListener;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.abstracto.core.models.listener.ChannelGroupDeletedListenerModel;
import dev.sheldan.abstracto.core.service.management.ChannelGroupManagementService;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQServiceBean;
import dev.sheldan.oneplus.bot.modules.faq.service.management.FAQChannelGroupManagementServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FAQChannelGroupDeletedListener implements ChannelGroupDeletedListener {

    @Autowired
    private FAQChannelGroupManagementServiceBean faqChannelGroupManagementServiceBean;

    @Autowired
    private ChannelGroupManagementService channelGroupManagementService;

    @Override
    public DefaultListenerResult execute(ChannelGroupDeletedListenerModel model) {
        AChannelGroup channelGroup = channelGroupManagementService.findChannelGroupById(model.getChannelGroupId());
        if(channelGroup.getChannelGroupType().getGroupTypeKey().equals(FAQServiceBean.FAQ_CHANNEL_GROUP_KEY)) {
            faqChannelGroupManagementServiceBean.deleteFAQChannelGroup(channelGroup);
            return DefaultListenerResult.PROCESSED;
        }
        return DefaultListenerResult.IGNORED;
    }
}
