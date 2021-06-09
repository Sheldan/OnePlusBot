package dev.sheldan.oneplus.bot.modules.faq.service.management;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.FaqCommandConfig;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.repository.FAQCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class FAQCommandManagementServiceBean {
    @Autowired
    private FAQCommandRepository repository;

    @Autowired
    private FAQCommandAliasManagementService faqCommandAliasManagementService;

    @Autowired
    private FAQChannelGroupCommandManagementServiceBean faqChannelGroupCommandManagementServiceBean;

    public Optional<FAQCommand> findByNameAndServer(String name, AServer server) {
        return repository.findByNameEqualsIgnoreCaseAndServer(name, server);
    }

    public List<FAQCommand> findAllInServer(AServer server) {
        return repository.findByServer(server);
    }

    public FAQCommand createFAQCommand(String name, Boolean global, AServer server) {
        FAQCommand command = FAQCommand
                .builder()
                .name(name)
                .server(server)
                .global(global)
                .build();
        log.info("Creating FAQ Command in server {}.", server.getId());
        return repository.save(command);
    }

    public FAQCommand createFAQCommand(FaqCommandConfig faqCommandConfig, AServer server) {
        return createFAQCommand(faqCommandConfig.getFaqCommandName(), faqCommandConfig.getGlobal(), server);
    }

    public void deleteFAQCommand(FAQCommand faqCommand) {
        faqChannelGroupCommandManagementServiceBean.deleteFAQChannelGroupCommands(faqCommand.getGroupResponses());
        faqCommandAliasManagementService.deleteFAQCommandAliases(faqCommand);
        log.info("Deleting FAQ command {}.", faqCommand.getId());
        repository.delete(faqCommand);
    }

    public List<FAQCommand> findGlobalCommandsInServer(AServer server) {
        return repository.findByServerAndGlobalTrue(server);
    }
}
