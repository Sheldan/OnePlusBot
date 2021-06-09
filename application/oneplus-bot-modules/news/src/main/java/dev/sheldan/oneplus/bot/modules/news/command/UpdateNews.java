package dev.sheldan.oneplus.bot.modules.news.command;

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
public class UpdateNews extends AbstractConditionableCommand {

    @Autowired
    private NewsServiceBean newsServiceBean;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        Long messageId = (Long) parameters.get(0);
        String postText = (String) parameters.get(1);
        // workaround for Discord formatting issue
        postText = postText.replace("\n >", "\n>");
        return newsServiceBean.updateNewsPostViaId(messageId, postText, commandContext.getMessage())
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter newsPostId = Parameter.builder().name("newsPostId").type(Long.class).templated(true).build();
        Parameter newsText = Parameter.builder().name("text").type(String.class).remainder(true).templated(true).build();
        List<Parameter> parameters = Arrays.asList(newsPostId, newsText);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("updateNews")
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
