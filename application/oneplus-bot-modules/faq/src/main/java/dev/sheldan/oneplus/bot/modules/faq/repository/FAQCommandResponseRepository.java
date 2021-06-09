package dev.sheldan.oneplus.bot.modules.faq.repository;

import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.CommandResponseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQCommandResponseRepository extends JpaRepository<FAQCommandResponse, CommandResponseId> {
}
