package dev.sheldan.oneplus.bot.modules.faq.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQModuleDefinition;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class DeleteFAQ extends AbstractConditionableCommand {

    @Autowired
    private FAQServiceBean faqServiceBean;

    @Autowired
    private ServerManagementService serverManagementService;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameter = commandContext.getParameters().getParameters();
        String faqCommandName = (String) parameter.get(0);
        AServer server = serverManagementService.loadServer(commandContext.getGuild());
        faqServiceBean.deleteFAQCommand(faqCommandName, server);
        return CompletableFuture.completedFuture(CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter fileAttachmentParameter = Parameter.builder().name("commandName").type(String.class).templated(true).build();
        List<Parameter> parameters = Arrays.asList(fileAttachmentParameter);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("deleteFAQ")
                .module(FAQModuleDefinition.FAQ)
                .parameters(parameters)
                .supportsEmbedException(true)
                .help(helpInfo)
                .async(true)
                .templated(true)
                .causesReaction(true)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return FAQFeatureDefinition.FAQ;
    }
}
