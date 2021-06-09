package dev.sheldan.oneplus.bot.modules.faq.repository;

import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.ChannelGroupCommandId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQChannelGroupCommandRepository extends JpaRepository<FAQChannelGroupCommand, ChannelGroupCommandId> {
    List<FAQChannelGroupCommand> findByChannelGroup(FAQChannelGroup faqChannelGroup);
    List<FAQChannelGroupCommand> findByChannelGroup_IdIn(List<Long> channelGroupIds);
    List<FAQChannelGroupCommand> findByChannelGroupIn(List<FAQChannelGroup> faqChannelGroup);
}
