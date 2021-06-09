package dev.sheldan.oneplus.bot.modules.faq.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.config.FeatureMode;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureMode;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQModuleDefinition;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faquses.FAQUsageModel;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQUsageServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class FAQUsage extends AbstractConditionableCommand {

    @Autowired
    private FAQUsageServiceBean faqUsageServiceBean;

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private ChannelService channelService;

    private static final String FAQ_USAGE_RESPONSE_TEMPLATE_KEY = "FAQUsage_response";

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        FAQUsageModel model;
        AServer server = serverManagementService.loadServer(commandContext.getGuild());
        if(parameters.isEmpty()) {
            model = faqUsageServiceBean.getFAQUsageModel(server);
        } else {
            String commandName = (String) parameters.get(0);
            model = faqUsageServiceBean.getFAQUsageModel(server, commandName);
        }

        return FutureUtils.toSingleFutureGeneric(channelService.sendEmbedTemplateInTextChannelList(FAQ_USAGE_RESPONSE_TEMPLATE_KEY, model, commandContext.getChannel()))
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter commandNameParameter = Parameter.builder().name("commandName").type(String.class).templated(true).optional(true).build();
        List<Parameter> parameters = Arrays.asList(commandNameParameter);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("FAQUsage")
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

    @Override
    public List<FeatureMode> getFeatureModeLimitations() {
        return Arrays.asList(FAQFeatureMode.FAQ_USES);
    }
}
