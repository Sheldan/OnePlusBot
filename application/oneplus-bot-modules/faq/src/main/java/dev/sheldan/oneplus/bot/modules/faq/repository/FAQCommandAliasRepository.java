package dev.sheldan.oneplus.bot.modules.faq.repository;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandAlias;
import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.FAQCommandAliasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FAQCommandAliasRepository extends JpaRepository<FAQCommandAlias, FAQCommandAliasId> {
    Optional<FAQCommandAlias> findById_AliasAndCommand_Server(String alias, AServer server);
}
