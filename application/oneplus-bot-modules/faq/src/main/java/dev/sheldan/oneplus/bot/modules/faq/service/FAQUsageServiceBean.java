package dev.sheldan.oneplus.bot.modules.faq.service;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.faq.exception.FAQCommandNotFoundException;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faquses.FAQCommandResponseUsage;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faquses.FAQCommandUsage;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faquses.FAQUsageModel;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.service.management.FAQCommandManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FAQUsageServiceBean {

    @Autowired
    private FAQCommandManagementServiceBean faqCommandManagementServiceBean;

    public FAQUsageModel getFAQUsageModel(AServer server, String faqCommandName) {
        FAQCommand faqCommand = faqCommandManagementServiceBean.findByNameAndServer(faqCommandName, server)
                .orElseThrow(() -> new FAQCommandNotFoundException(faqCommandName));
        List<FAQCommandUsage> commandUsages = Arrays.asList(getFAQCommandUsage(faqCommand));
        return FAQUsageModel
                .builder()
                .uses(commandUsages)
                .build();
    }

    public FAQUsageModel getFAQUsageModel(AServer server) {
        List<FAQCommand> allCommands = faqCommandManagementServiceBean.findAllInServer(server);
        List<FAQCommandUsage> commandUsages = allCommands
                .stream()
                .map(this::getFAQCommandUsage)
                .collect(Collectors.toList());
        return FAQUsageModel
                .builder()
                .uses(commandUsages)
                .build();
    }

    private FAQCommandUsage getFAQCommandUsage(FAQCommand command) {
        List<FAQCommandResponseUsage> channelGroupUsages = command
                .getGroupResponses()
                .stream()
                .map(this::getFAQCommandResponseUsage)
                .collect(Collectors.toList());
        return FAQCommandUsage
                .builder()
                .faqCommandName(command.getName())
                .faqChannelGroupUsages(channelGroupUsages)
                .build();
    }

    private FAQCommandResponseUsage getFAQCommandResponseUsage(FAQChannelGroupCommand groupCommand) {
        return FAQCommandResponseUsage
                .builder()
                .uses(groupCommand.getUses())
                .channelGroupName(groupCommand.getChannelGroup().getChannelGroup().getGroupName())
                .build();
    }
}
