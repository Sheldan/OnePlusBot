package dev.sheldan.oneplus.bot.modules.faq.service.management;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandAlias;
import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.FAQCommandAliasId;
import dev.sheldan.oneplus.bot.modules.faq.repository.FAQCommandAliasRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class FAQCommandAliasManagementService {

    @Autowired
    private FAQCommandAliasRepository commandAliasRepository;

    public FAQCommandAlias createFAQCommandAlias(FAQCommand command, String aliasName) {
        FAQCommandAlias alias = FAQCommandAlias
                .builder()
                .command(command)
                .id(FAQCommandAliasId
                        .builder()
                        .faqCommandId(command.getId())
                        .alias(aliasName)
                        .build())
                .build();
        log.info("Creating faq command alias for command {}.", command.getId());
        return commandAliasRepository.save(alias);
    }

    public void deleteFAQCommandAlias(FAQCommand command, String aliasName) {
        FAQCommandAliasId aliasId = FAQCommandAliasId
                .builder()
                .faqCommandId(command.getId())
                .alias(aliasName)
                .build();
        log.info("Deleting faq command alias for command {}.", command.getId());
        commandAliasRepository.deleteById(aliasId);
    }

    public void deleteFAQCommandAliases(FAQCommand faqCommand){
        log.info("deleting all FAQ command aliases for command {}.", faqCommand.getId());
        commandAliasRepository.deleteAll(faqCommand.getAliases());
    }

    public Optional<FAQCommandAlias> findByNameAndServer(String name, AServer server) {
        return commandAliasRepository.findById_AliasAndCommand_Server(name, server);
    }
}
