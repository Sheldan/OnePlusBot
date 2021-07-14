package dev.sheldan.oneplus.bot.custom.moderation.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.moderation.config.ModerationModuleDefinition;
import dev.sheldan.oneplus.bot.custom.moderation.config.ModerationCustomFeatureDefinition;
import dev.sheldan.oneplus.bot.custom.moderation.service.ModModeServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Component
public class ModMode extends AbstractConditionableCommand {

    @Autowired
    private ModModeServiceBean modModeServiceBean;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        Boolean newState = (Boolean) commandContext.getParameters().getParameters().get(0);
        return modModeServiceBean.setModModeTo(commandContext.getGuild(), newState)
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter newStateParameter = Parameter.builder().name("newState").templated(true).type(Boolean.class).build();
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("modMode")
                .async(true)
                .module(ModerationModuleDefinition.MODERATION)
                .parameters(Collections.singletonList(newStateParameter))
                .help(helpInfo)
                .templated(true)
                .supportsEmbedException(true)
                .causesReaction(true)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return ModerationCustomFeatureDefinition.MODERATION_CUSTOM;
    }
}
