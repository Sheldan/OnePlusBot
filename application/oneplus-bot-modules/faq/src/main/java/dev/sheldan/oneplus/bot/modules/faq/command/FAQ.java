package dev.sheldan.oneplus.bot.modules.faq.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.management.ChannelManagementService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQModuleDefinition;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faq.FAQResponseModel;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQResponseServiceBean;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class FAQ extends AbstractConditionableCommand {

    @Autowired
    private FAQResponseServiceBean faqService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private ChannelManagementService channelManagementService;

    private static final String FAQ_RESPONSE_TEMPLATE_KEY = "FAQ_response";
    private static final String FAQ_RESPONSE_NO_COMMAND_FOUND_TEMPLATE_KEY = "FAQ_response_no_command_found";

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        String commandName;
        if(!parameters.isEmpty()) {
            commandName = (String) parameters.get(0);
            GuildMessageChannel channel;
            if (parameters.size() == 2) {
                channel = (TextChannel) parameters.get(1);
            } else {
                channel = commandContext.getChannel();
            }
            return faqService.loadFAQResponse(commandName, channel)
                    .thenCompose(faqResponseModel -> {
                        if (!faqResponseModel.getMessages().isEmpty()) {
                            List<CompletableFuture<Void>> messageFutures = new ArrayList<>();
                            faqResponseModel
                                    .getMessages()
                                    .forEach(faqResponseMessageModel ->
                                            messageFutures.add(FutureUtils.
                                                    toSingleFutureGeneric(channelService.
                                                            sendEmbedTemplateInTextChannelList(FAQ_RESPONSE_TEMPLATE_KEY,
                                                                    faqResponseMessageModel, commandContext.getChannel()))));
                            return FutureUtils.toSingleFutureGeneric(messageFutures);
                        } else {
                            return FutureUtils
                                    .toSingleFutureGeneric(
                                            channelService.
                                                    sendEmbedTemplateInTextChannelList(FAQ_RESPONSE_NO_COMMAND_FOUND_TEMPLATE_KEY,
                                                            faqResponseModel, commandContext.getChannel()));
                        }
                    })
                    .thenApply(unused -> CommandResult.fromSuccess());
        } else {
            AServer server = serverManagementService.loadServer(commandContext.getGuild());
            AChannel channel = channelManagementService.loadChannel(commandContext.getChannel());
            FAQResponseModel model = faqService.getNoCommandFoundModel(server, channel);
            return FutureUtils
                    .toSingleFutureGeneric(
                            channelService.
                                    sendEmbedTemplateInTextChannelList(FAQ_RESPONSE_NO_COMMAND_FOUND_TEMPLATE_KEY,
                                            model, commandContext.getChannel()))
                    .thenApply(unused -> CommandResult.fromSuccess());
        }
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter commandNameParameter = Parameter.builder().name("command").type(String.class).templated(true).optional(true).build();
        Parameter channelParameter = Parameter.builder().name("channel").type(TextChannel.class).optional(true).templated(true).build();
        List<Parameter> parameters = Arrays.asList(commandNameParameter, channelParameter);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("FAQ")
                .module(FAQModuleDefinition.FAQ)
                .parameters(parameters)
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
