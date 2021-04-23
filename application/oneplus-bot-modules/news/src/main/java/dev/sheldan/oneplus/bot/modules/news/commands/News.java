package dev.sheldan.oneplus.bot.modules.news.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.oneplus.bot.modules.news.config.NewsFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.news.config.NewsModuleDefinition;
import dev.sheldan.oneplus.bot.modules.news.service.NewsServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class News extends AbstractConditionableCommand {

    @Autowired
    private NewsServiceBean newsServiceBean;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        String text = (String) commandContext.getParameters().getParameters().get(0);
        return newsServiceBean.sendNewsPost(text, commandContext.getMessage())
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter newsText = Parameter.builder().name("text").type(String.class).remainder(true).templated(true).build();
        List<Parameter> parameters = Arrays.asList(newsText);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).hasExample(true).build();
        return CommandConfiguration.builder()
                .name("news")
                .module(NewsModuleDefinition.NEWS)
                .parameters(parameters)
                .supportsEmbedException(true)
                .async(true)
                .help(helpInfo)
                .templated(true)
                .causesReaction(true)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return NewsFeatureDefinition.NEWS;
    }
}
