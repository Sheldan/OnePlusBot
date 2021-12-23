package dev.sheldan.oneplus.bot.modules.seasonal.lights.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.exception.EntityGuildMismatchException;
import dev.sheldan.oneplus.bot.modules.seasonal.config.SeasonalEntertainmentFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.seasonal.config.SeasonalFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.service.LightsRoleServiceBean;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RemoveRoleFromLights extends AbstractConditionableCommand {

    @Autowired
    private LightsRoleServiceBean lightsMemberServiceBean;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        Role role = (Role) parameters.get(0);
        if(!role.getGuild().equals(commandContext.getGuild())) {
            throw new EntityGuildMismatchException();
        }
        lightsMemberServiceBean.removeMemberFromSeasonalLights(role);
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Parameter.builder().name("role").templated(true).type(Role.class).build());
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("removeRoleFromLights")
                .module(SeasonalEntertainmentFeatureDefinition.ENTERTAINMENT)
                .templated(true)
                .supportsEmbedException(true)
                .causesReaction(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return SeasonalFeatureDefinition.LIGHTS;
    }
}
