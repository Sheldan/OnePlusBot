package dev.sheldan.oneplus.bot.modules.faq.repository;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FAQChannelGroupRepository extends JpaRepository<FAQChannelGroup, Long> {
    Optional<FAQChannelGroup> findByChannelGroup_ServerAndGlobalTrue(AServer server);
}
