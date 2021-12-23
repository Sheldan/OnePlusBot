package dev.sheldan.oneplus.bot.modules.seasonal.lights.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.oneplus.bot.modules.seasonal.config.SeasonalEntertainmentFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.seasonal.config.SeasonalFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.service.LightsRoleServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class SwitchLights extends AbstractConditionableCommand {

    @Autowired
    private LightsRoleServiceBean lightsMemberServiceBean;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        return lightsMemberServiceBean.switchLights(commandContext.getGuild())
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        HelpInfo helpInfo = HelpInfo
                .builder()
                .templated(true)
                .build();
        return CommandConfiguration.builder()
                .name("switchLights")
                .module(SeasonalEntertainmentFeatureDefinition.ENTERTAINMENT)
                .templated(true)
                .async(true)
                .causesReaction(true)
                .supportsEmbedException(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return SeasonalFeatureDefinition.LIGHTS;
    }
}
