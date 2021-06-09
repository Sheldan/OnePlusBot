package dev.sheldan.oneplus.bot.modules.faq.service;

import com.google.gson.Gson;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.UserService;
import dev.sheldan.abstracto.core.service.management.ChannelGroupManagementService;
import dev.sheldan.oneplus.bot.modules.faq.converter.FAQCommandConfigConverter;
import dev.sheldan.oneplus.bot.modules.faq.converter.ListFAQCommandsModelConverter;
import dev.sheldan.oneplus.bot.modules.faq.exception.*;
import dev.sheldan.oneplus.bot.modules.faq.models.command.config.*;
import dev.sheldan.oneplus.bot.modules.faq.models.command.list.ListFAQCommandsModel;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import dev.sheldan.oneplus.bot.modules.faq.service.management.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FAQServiceBean {

    public static final String FAQ_CHANNEL_GROUP_KEY = "faq";

    @Autowired
    private ChannelGroupManagementService channelGroupManagementService;

    @Autowired
    private Gson gson;

    @Autowired
    private FAQCommandManagementServiceBean faqCommandManagementServiceBean;

    @Autowired
    private FAQChannelGroupManagementServiceBean faqChannelGroupManagementServiceBean;

    @Autowired
    private FAQCommandResponseManagementServiceBean faqCommandResponseManagementServiceBean;

    @Autowired
    private FAQChannelGroupCommandManagementServiceBean faqChannelGroupCommandManagementServiceBean;

    @Autowired
    private FAQCommandAliasManagementService faqCommandAliasManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private FAQCommandConfigConverter configConverter;

    @Autowired
    private ListFAQCommandsModelConverter listFAQCommandsModelConverter;

    public void createOrUpdateFAQCommands(List<FaqCommandConfig> commands, AServer server) {
        fillDefaults(commands);
        validateCommandConfig(commands, server);
        commands.forEach(faqCommandConfig -> handleFAQCommand(faqCommandConfig, server));
    }

    private void validateCommandConfig(List<FaqCommandConfig> commands, AServer server) {
        List<FaqCommandConfig> incorrectGlobalConfigurations = commands
                .stream()
                .filter(faqCommandConfig -> faqCommandConfig.getGlobal() && faqCommandConfig.getResponses().size() > 1)
                .collect(Collectors.toList());
        if(!incorrectGlobalConfigurations.isEmpty()) {
            List<String> commandsWithIllegalConfiguration = incorrectGlobalConfigurations
                    .stream()
                    .map(FaqCommandConfig::getFaqCommandName)
                    .collect(Collectors.toList());
            throw new GlobalFAQCommandResponsesException(commandsWithIllegalConfiguration);
        }

        List<String> toBeConfiguredCommandNames = new ArrayList<>();
        commands.forEach(faqCommandConfig -> {
            toBeConfiguredCommandNames.add(faqCommandConfig.getFaqCommandName());
            if(faqCommandConfig.getAliases() != null) {
                toBeConfiguredCommandNames.addAll(faqCommandConfig.getAliases());
            }
        });

        Set<String> uniqueToBeConfiguredCommandNames = new HashSet<>(toBeConfiguredCommandNames);
        if(toBeConfiguredCommandNames.size() != uniqueToBeConfiguredCommandNames.size()) {
            List<String> duplicatedKeys = getDuplicatedKeys(toBeConfiguredCommandNames);
            throw new DuplicatedCommandNameOrAliasException(duplicatedKeys);
        }
        List<FAQCommand> allCommands = faqCommandManagementServiceBean.findAllInServer(server);
        Map<String, FAQCommand> aliasMappings = getAliasMappings(allCommands);
        commands.forEach(faqCommandConfig -> {
            if(faqCommandConfig.getAliases() == null) return;
            // find a command in which the alias is used as alias
            faqCommandConfig.getAliases().forEach(aliasName -> {
                if(aliasMappings.containsKey(aliasName)) {
                    FAQCommand faqCommand = aliasMappings.get(aliasName);
                    // but the command should be a different one, else we would find ourselves
                    if(!faqCommand.getName().equals(faqCommandConfig.getFaqCommandName())) {
                        throw new DuplicatedFAQCommandAliasException(faqCommand.getName(), aliasName, faqCommandConfig.getFaqCommandName());
                    }
                }
            });
        });
        Map<String, FAQCommand> commandMappings = getCommandMappings(allCommands);
        commands.forEach(faqCommandConfig -> {
            if(faqCommandConfig.getAliases() == null) return;
            // check if the to be configured aliases clash with an existing command
            faqCommandConfig.getAliases().forEach(aliasName -> {
                if(commandMappings.containsKey(aliasName)) {
                    throw new FAQCommandAliasShadowingException(faqCommandConfig.getFaqCommandName(), aliasName);
                }
            });
        });
    }

    private List<String> getDuplicatedKeys(List<String> toBeConfiguredCommandNames) {
        Map<String, Long> collections = toBeConfiguredCommandNames.stream().collect(
                Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()));
        List<String> duplicatedKeys = new ArrayList<>();
        collections.forEach((s, aLong) -> {
            if(aLong > 1) {
                duplicatedKeys.add(s);
            }
        });
        return duplicatedKeys;
    }

    private void handleFAQCommand(FaqCommandConfig faqCommandConfig, AServer server) {
        Optional<FAQCommand> existingFAQCommandOptional = faqCommandManagementServiceBean.findByNameAndServer(faqCommandConfig.getFaqCommandName(), server);
        FAQCommand commandInstance;
        if(existingFAQCommandOptional.isPresent()) {
            commandInstance = existingFAQCommandOptional.get();
            log.debug("Updating command {} in server {}.", commandInstance.getId(), server.getId());
            updateFAQCommand(faqCommandConfig, commandInstance);
        } else {
            log.debug("Creating new command in server {}.", server.getId());
            commandInstance = createFAQCommand(faqCommandConfig, server);
        }
        Set<String> existingChannelGroupNames =
                commandInstance.getGroupResponses()
                .stream()
                .map(faqChannelGroupCommand -> faqChannelGroupCommand.getChannelGroup().getChannelGroup().getGroupName())
                .collect(Collectors.toSet());
        Set<String> configuredChannelGroupNames = new HashSet<>();
        faqCommandConfig
                .getResponses()
                .forEach(faqCommandResponseConfig -> {
                    handleFAQChannelGroupCommand(faqCommandResponseConfig, commandInstance, server);
                    configuredChannelGroupNames.add(faqCommandResponseConfig.getChannelGroupName());
                });
        // this deletes any channel group responses which were not reflected in the configuration
        existingChannelGroupNames.removeAll(configuredChannelGroupNames);
        existingChannelGroupNames.forEach(channelGroupName -> {
            AChannelGroup channelGroup = channelGroupManagementService.findByNameAndServerAndType(channelGroupName, server, FAQ_CHANNEL_GROUP_KEY);
            FAQChannelGroup faqChannelGroup = faqChannelGroupManagementServiceBean.getFAQChannelGroupById(channelGroup.getId());
            faqChannelGroupCommandManagementServiceBean.deleteFAQChannelGroupCommand(commandInstance, faqChannelGroup);
        });
    }

    private void handleFAQChannelGroupCommand(FaqCommandResponseConfig responseConfig, FAQCommand command, AServer server) {
        AChannelGroup channelGroup = channelGroupManagementService.findByNameAndServerAndType(responseConfig.getChannelGroupName(), server, FAQ_CHANNEL_GROUP_KEY);
        FAQChannelGroup faqChannelGroup = faqChannelGroupManagementServiceBean.getFAQChannelGroupById(channelGroup.getId());
        Optional<FAQChannelGroupCommand> existingChannelGroupCommandOptional = command.getGroupResponses()
                .stream()
                .filter(faqChannelGroupCommand -> faqChannelGroupCommand.getChannelGroup().getChannelGroup()
                        .equals(faqChannelGroup.getChannelGroup()))
                .findAny();
        if(command.getGlobal() && !faqChannelGroup.getGlobal()) {
            log.warn("Command {} is configured as global, but to be used channel group is not defined as the global channel group.", command.getId());
            throw new GlobalFAQCommandConfigMismatchException(command.getName());
        }
        if(faqChannelGroup.getGlobal() && !command.getGlobal()) {
            log.warn("Channel group to be used is configured as global, but command {} is not. This is an illegal combination.", command.getId());
            throw new GlobalFAQCommandConfigMismatchException(command.getName());
        }
        if(existingChannelGroupCommandOptional.isPresent()) {
            FAQChannelGroupCommand existing = existingChannelGroupCommandOptional.get();
            log.debug("Updating faq channel group command {} for channel group {} in server {}.", command.getId(), faqChannelGroup.getId(), server.getId());
            updateFAQChannelGroupCommand(existing, responseConfig);
        } else {
            log.debug("Creating new faq channel group command for command {} in channel group {} in server {}.", command.getId(), faqChannelGroup.getId(), server.getId());
            createFAQChannelGroupCommand(command, faqChannelGroup, responseConfig);
        }
    }

    private void createFAQChannelGroupCommand(FAQCommand faqCommand, FAQChannelGroup faqChannelGroup, FaqCommandResponseConfig responseConfig) {
        FAQChannelGroupCommand channelGroupCommand = faqChannelGroupCommandManagementServiceBean.createFAQChannelGroupCommand(faqCommand, faqChannelGroup);
        log.debug("Creating {} messages.", responseConfig.getMessages().size());
        for (int i = 0; i < responseConfig.getMessages().size(); i++) {
            createFAQResponses(channelGroupCommand, responseConfig.getMessages().get(i), i);
        }
    }

    private void updateFAQChannelGroupCommand(FAQChannelGroupCommand existing, FaqCommandResponseConfig responseConfig) {
        Set<Integer> positions = responseConfig
                .getMessages()
                .stream()
                .map(FaqCommandResponseMessageConfig::getPosition)
                .collect(Collectors.toSet());

        if(positions.size() != responseConfig.getMessages().size()) {
            throw new FAQCommandResponseDuplicatedPositionException(existing.getCommand().getName(), responseConfig.getChannelGroupName());
        }

        List<FAQCommandResponse> existingResponses = existing
                .getResponses()
                .stream()
                .sorted(Comparator.comparing(o -> o.getId().getPosition()))
                .collect(Collectors.toList());

        List<FaqCommandResponseMessageConfig> configMessages = responseConfig
                .getMessages()
                .stream()
                .sorted(Comparator.comparingInt(FaqCommandResponseMessageConfig::getPosition))
                .collect(Collectors.toList());

        // re-use the existing entries, and only update if necessary, so have the appropriate update timestamp
        int iterations = Math.min(configMessages.size(), existingResponses.size());
        for (int i = 0; i < iterations; i++) {
            mergeConfig(existingResponses.get(i), configMessages.get(i));
        }

        // remove all the rest, as they were not specified in the config
        for(int i = iterations; i < existingResponses.size(); i++) {
            faqCommandResponseManagementServiceBean.deleteCommandResponse(existingResponses.get(i));
        }

        for(int i = iterations; i < configMessages.size(); i++) {
            createFAQResponses(existing, configMessages.get(i), i);
        }

    }

    private void createFAQResponses(FAQChannelGroupCommand existing, FaqCommandResponseMessageConfig config, int position) {
        faqCommandResponseManagementServiceBean.createResponse(config, position, existing);
    }

    private void mergeConfig(FAQCommandResponse existingResponse, FaqCommandResponseMessageConfig config) {
        if(existingResponse.getAdditionalMessage() == null || !existingResponse.getAdditionalMessage().equals(config.getAdditionalMessage())) {
            existingResponse.setAdditionalMessage(config.getAdditionalMessage());
        }
        FaqCommandResponseEmbedConfig embed = config.getEmbed();
        if(embed != null) {
            if(existingResponse.getDescription() == null || !existingResponse.getDescription().equals(embed.getDescription())) {
                existingResponse.setDescription(embed.getDescription());
            }
            if(existingResponse.getImageURL() == null || !existingResponse.getImageURL().equals(embed.getImageUrl())) {
                existingResponse.setImageURL(embed.getImageUrl());
            }
            if(existingResponse.getAuthorUserId() == null || !existingResponse.getAuthorUserId().equals(embed.getAuthor().getUserId())) {
                existingResponse.setAuthorUserId(embed.getAuthor().getUserId());
            }
            mergeEmbedColor(existingResponse, embed);
        } else {
            existingResponse.setDescription(null);
            existingResponse.setImageURL(null);
            existingResponse.setAuthorUserId(null);
        }
    }

    private void mergeEmbedColor(FAQCommandResponse existingResponse, FaqCommandResponseEmbedConfig embed) {
        FaqCommandResponseEmbedColorConfig embedColor = embed.getColor();
        if(existingResponse.getBlue() == null || !existingResponse.getBlue().equals(embedColor.getBlue())) {
            existingResponse.setBlue(embedColor.getBlue());
        }
        if(existingResponse.getGreen() == null || !existingResponse.getGreen().equals(embedColor.getGreen())){
            existingResponse.setGreen(embedColor.getGreen());
        }
        if(existingResponse.getRed() == null || !existingResponse.getRed().equals(embedColor.getRed())) {
            existingResponse.setRed(embedColor.getRed());
        }
    }

    private void updateFAQCommand(FaqCommandConfig faqCommandConfig, FAQCommand command){
        if(!faqCommandConfig.getGlobal().equals(command.getGlobal())) {
            command.setGlobal(faqCommandConfig.getGlobal());
        }
        Set<String> existingAlias;
        if(command.getAliases() != null) {
            existingAlias = command
                    .getAliases()
                    .stream()
                    .map(faqCommandAlias -> faqCommandAlias.getId().getAlias())
                    .collect(Collectors.toSet());
        } else {
            existingAlias = new HashSet<>();
        }

        Set<String> toBeConfigured;
        if(faqCommandConfig.getAliases() != null) {
            toBeConfigured = new HashSet<>(faqCommandConfig.getAliases());
        } else {
            toBeConfigured = new HashSet<>();
        }
        Set<String> aliasNeedingCreation = new HashSet<>(toBeConfigured);
        aliasNeedingCreation.removeAll(existingAlias);
        log.info("Creating {} aliases for command {}.", aliasNeedingCreation.size(), command.getId());
        createFAQCommandAlias(command, aliasNeedingCreation);
        Set<String> aliasToDelete = new HashSet<>(existingAlias);
        aliasToDelete.removeAll(toBeConfigured);
        log.info("Removing {} aliases for command {}.", aliasToDelete.size(), command.getId());
        deleteFAQCommandAlias(command, aliasToDelete);
    }

    private void createFAQCommandAlias(FAQCommand command, Collection<String> aliasNames) {
        aliasNames.forEach(s -> faqCommandAliasManagementService.createFAQCommandAlias(command, s));
    }

    private void deleteFAQCommandAlias(FAQCommand command, Collection<String> aliasName) {
        aliasName.forEach(s -> faqCommandAliasManagementService.deleteFAQCommandAlias(command, s));
    }

    private FAQCommand createFAQCommand(FaqCommandConfig faqCommandConfig, AServer server) {
        FAQCommand faqCommand = faqCommandManagementServiceBean.createFAQCommand(faqCommandConfig, server);
        if(faqCommandConfig.getAliases() != null) {
            createFAQCommandAlias(faqCommand, faqCommandConfig.getAliases());
        }
        return faqCommand;
    }

    private void fillDefaults(List<FaqCommandConfig> commands) {
        commands
                .forEach(faqCommandConfig ->  {
                    if(faqCommandConfig.getGlobal() == null) {
                        faqCommandConfig.setGlobal(Boolean.FALSE);
                    }
                    faqCommandConfig
                        .getResponses()
                        .forEach(faqCommandResponse -> faqCommandResponse
                                .getMessages().forEach(
                                        faqCommandResponseMessage -> {
                                            FaqCommandResponseEmbedConfig embed = faqCommandResponseMessage.getEmbed();
                                            if(embed == null) {
                                                return;
                                            }
                                            FaqCommandResponseEmbedColorConfig color = embed.getColor();
                                            if(color == null) {
                                                color = FaqCommandResponseEmbedColorConfig.builder().build();
                                                embed.setColor(color);
                                            }
                                            if(color.getBlue() == null || color.getBlue() == 0) {
                                                color.setBlue(0);
                                            }
                                            if(color.getRed() == null || color.getRed() == 0) {
                                                color.setRed(0);
                                            }
                                            if(color.getGreen() == null || color.getGreen() == 0) {
                                                color.setGreen(0);
                                            }
                                            if(embed.getAuthor().getUseBot() != null && embed.getAuthor().getUseBot()) {
                                                log.debug("Setting the BOT as the author.");
                                                embed.getAuthor().setUserId(userService.getSelfUser().getIdLong());
                                            }
                                        }
                                ));
                });
    }

    private Map<String, FAQCommand> getCommandMappings(List<FAQCommand> commands) {
        return commands
                .stream()
                .collect(Collectors.toMap(FAQCommand::getName, Function.identity()));
    }

    private Map<String, FAQCommand> getAliasMappings(List<FAQCommand> commands) {
        Map<String, FAQCommand> commandMappings = commands
                .stream()
                .collect(Collectors.toMap(FAQCommand::getName, Function.identity()));
        commands
                .forEach(faqCommand -> faqCommand
                        .getAliases()
                        .forEach(faqCommandAlias ->
                                commandMappings.put(faqCommandAlias.getId().getAlias(), faqCommand)));
        return commandMappings;
    }

    public List<FaqCommandConfig> loadFAQCommandsFromJson(String json) {
        FaqCommandConfig[] faqCommandConfigs = gson.fromJson(json, FaqCommandConfig[].class);
        return Arrays.asList(faqCommandConfigs);
    }

    public void deleteFAQCommand(String commandToDelete, AServer server) {
        FAQCommand toDelete = faqCommandManagementServiceBean.findByNameAndServer(commandToDelete, server)
                .orElseThrow(() -> new FAQCommandNotFoundException(commandToDelete));
        faqCommandManagementServiceBean.deleteFAQCommand(toDelete);
    }

    public String exportFAQConfig(String commandToExport, AServer server) {
        FAQCommand toExport = faqCommandManagementServiceBean.findByNameAndServer(commandToExport, server)
                .orElseThrow(() -> new FAQCommandNotFoundException(commandToExport));
        return configConverter.serializeCommands(Arrays.asList(toExport));
    }

    public String exportFAQConfig(AServer server) {
        List<FAQCommand> toExport = faqCommandManagementServiceBean.findAllInServer(server);
        log.info("Exporting {} FAQ commands for server {}.", toExport.size(), server.getId());
        return configConverter.serializeCommands(toExport);
    }

    public ListFAQCommandsModel getCommandListingForServer(AServer server) {
        List<FAQCommand> faqCommands = faqCommandManagementServiceBean.findAllInServer(server);
        return listFAQCommandsModelConverter.fromFAQCommands(faqCommands);
    }
}
