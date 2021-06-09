package dev.sheldan.oneplus.bot.modules.faq.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQModuleDefinition;
import dev.sheldan.oneplus.bot.modules.faq.models.command.list.ListFAQCommandsModel;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ListFAQCommands extends AbstractConditionableCommand {

    @Autowired
    private FAQServiceBean faqServiceBean;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ServerManagementService serverManagementService;

    private static final String LIST_FAQ_COMMANDS_RESPONSE_TEMPLATE_KEY = "listFAQCommands_response";

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        AServer server = serverManagementService.loadServer(commandContext.getGuild());
        ListFAQCommandsModel model = faqServiceBean.getCommandListingForServer(server);
        return FutureUtils.toSingleFutureGeneric(channelService.sendEmbedTemplateInTextChannelList(LIST_FAQ_COMMANDS_RESPONSE_TEMPLATE_KEY, model, commandContext.getChannel()))
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("listFAQCommands")
                .module(FAQModuleDefinition.FAQ)
                .supportsEmbedException(true)
                .help(helpInfo)
                .async(true)
                .templated(true)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return FAQFeatureDefinition.FAQ;
    }
}
