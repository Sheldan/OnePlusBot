package dev.sheldan.oneplus.bot.modules.faq.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.exception.AbstractoTemplatedException;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.models.JSONValidationResult;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.JSONValidationService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.FileService;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQModuleDefinition;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.FaqCommandConfig;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQServiceBean;
import dev.sheldan.oneplus.bot.modules.faq.service.FAQValidationServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.everit.json.schema.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImportFAQ extends AbstractConditionableCommand {

    @Autowired
    private FileService fileService;

    @Autowired
    private FAQServiceBean faqServiceBean;

    @Autowired
    private FAQValidationServiceBean faqValidationServiceBean;
    
    @Autowired
    private ChannelService channelService;

    @Autowired
    private JSONValidationService jsonValidationService;

    @Autowired
    private ServerManagementService serverManagementService;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameter = commandContext.getParameters().getParameters();
        File jsonConfigFile = (File) parameter.get(0);
        try {
            String jsonContent = FileUtils.readFileToString(jsonConfigFile, StandardCharsets.UTF_8);
            JSONValidationResult result = faqValidationServiceBean.validateJSONForCreation(jsonContent);
            if(result.getResult().equals(JSONValidationService.Result.SUCCESSFUL)) {
                AServer server = serverManagementService.loadServer(commandContext.getGuild());
                List<FaqCommandConfig> commands = faqServiceBean.loadFAQCommandsFromJson(jsonContent);
                faqServiceBean.createOrUpdateFAQCommands(commands, server);
            } else {

                List<String> errors = jsonValidationService.getDetailedException(result.getExceptions())
                        .stream()
                        .map(ValidationException::getMessage)
                        .collect(Collectors.toList());
                channelService.sendTextToChannel(String.join("\n", errors), commandContext.getChannel());
            }
            return CompletableFuture.completedFuture(CommandResult.fromSuccess());
        } catch (IOException e) {
            log.error("IO Exception when loading input file.", e);
            throw new AbstractoTemplatedException("Failed to load json config.", "failed_to_set_template_exception", e);
        } finally {
            try {
                fileService.safeDelete(jsonConfigFile);
            } catch (IOException e) {
                log.error("Failed to delete downloaded json file.", e);
            }
        }
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter fileAttachmentParameter = Parameter.builder().name("file").type(File.class).templated(true).build();
        List<Parameter> parameters = Arrays.asList(fileAttachmentParameter);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("importFAQ")
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
