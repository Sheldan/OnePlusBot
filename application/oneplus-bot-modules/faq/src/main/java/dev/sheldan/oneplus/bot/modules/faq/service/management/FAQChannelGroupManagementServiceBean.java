package dev.sheldan.oneplus.bot.modules.faq.service.management;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.repository.FAQChannelGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class FAQChannelGroupManagementServiceBean {
    @Autowired
    private FAQChannelGroupRepository faqChannelGroupRepository;

    public Optional<FAQChannelGroup> findFAQChannelGroupByIdOptional(Long id) {
        return faqChannelGroupRepository.findById(id);
    }

    public FAQChannelGroup getFAQChannelGroupById(Long id) {
        return findFAQChannelGroupByIdOptional(id).orElseThrow(() -> new AbstractoRunTimeException("FAQ Channel group not found."));
    }

    public FAQChannelGroup createFAQChannelGroup(AChannelGroup channelGroup) {
        FAQChannelGroup faqChannelGroup = FAQChannelGroup
                .builder()
                .channelGroup(channelGroup)
                .global(false)
                .id(channelGroup.getId())
                .build();
        log.info("Creating FAQ channel group for channel group {} in server {}.", channelGroup.getId(), channelGroup.getServer().getId());
        return faqChannelGroupRepository.save(faqChannelGroup);
    }

    public Optional<FAQChannelGroup> getGlobalChannelGroup(AServer server) {
        return faqChannelGroupRepository.findByChannelGroup_ServerAndGlobalTrue(server);
    }

    public void deleteFAQChannelGroup(AChannelGroup channelGroup) {
        log.info("Deleting FAQ channel group {} in server {}.", channelGroup.getId(), channelGroup.getServer().getId());
        faqChannelGroupRepository.delete(getFAQChannelGroupById(channelGroup.getId()));
    }
}
