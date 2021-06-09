package dev.sheldan.oneplus.bot.modules.faq.repository;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FAQCommandRepository extends JpaRepository<FAQCommand, Long> {
    Optional<FAQCommand> findByNameEqualsIgnoreCaseAndServer(String name, AServer server);
    List<FAQCommand> findByNameInIgnoreCaseAndServer(List<String> names, AServer server);
    List<FAQCommand> findByServer(AServer server);
    List<FAQCommand> findByServerAndGlobalTrue(AServer server);
}
