package dev.sheldan.oneplus.bot.modules.faq.service;

import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.ChannelGroupService;
import dev.sheldan.abstracto.core.service.FeatureModeService;
import dev.sheldan.abstracto.core.service.UserService;
import dev.sheldan.abstracto.core.service.management.ChannelManagementService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.CompletableFutureList;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureDefinition;
import dev.sheldan.oneplus.bot.modules.faq.config.FAQFeatureMode;
import dev.sheldan.oneplus.bot.modules.faq.exception.NoFAQResponseFoundException;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faq.FAQResponseMessageModel;
import dev.sheldan.oneplus.bot.modules.faq.models.command.faq.FAQResponseModel;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandAlias;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import dev.sheldan.oneplus.bot.modules.faq.service.management.FAQChannelGroupCommandManagementServiceBean;
import dev.sheldan.oneplus.bot.modules.faq.service.management.FAQCommandAliasManagementService;
import dev.sheldan.oneplus.bot.modules.faq.service.management.FAQCommandManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class FAQResponseServiceBean {

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private ChannelGroupService channelGroupService;

    @Autowired
    private FAQCommandManagementServiceBean faqCommandManagementServiceBean;

    @Autowired
    private FAQChannelGroupCommandManagementServiceBean faqChannelGroupCommandManagementServiceBean;

    @Autowired
    private FAQCommandAliasManagementService faqCommandAliasManagementService;

    @Autowired
    private ChannelManagementService channelManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private FeatureModeService featureModeService;

    @Autowired
    private FAQResponseServiceBean self;

    public CompletableFuture<FAQResponseModel> loadFAQResponse(String commandName, GuildMessageChannel textChannel) {
        AServer server = serverManagementService.loadServer(textChannel.getGuild().getIdLong());
        Optional<FAQCommand> faqCommandOptional = faqCommandManagementServiceBean.findByNameAndServer(commandName, server);
        if(!faqCommandOptional.isPresent()) {
            log.debug("Did not find the command, trying via command alias.");
            Optional<FAQCommandAlias> faqCommandAlias = faqCommandAliasManagementService.findByNameAndServer(commandName, server);
            if(faqCommandAlias.isPresent()) {
                faqCommandOptional = Optional.of(faqCommandAlias.get().getCommand());
            }
        }
        AChannel channel = channelManagementService.loadChannel(textChannel.getIdLong());
        if (!faqCommandOptional.isPresent()) {
            log.debug("Did not find a faq response. Returning model with possible command names.");
            return CompletableFuture.completedFuture(getNoCommandFoundModel(server, channel));
        } else {
            FAQCommand command = faqCommandOptional.get();
            Optional<FAQChannelGroupCommand> groupCommand;
            if(command.getGlobal()) {
                groupCommand = command.getGroupResponses().stream().findAny();
            } else {
                Stream<FAQChannelGroupCommand> matchingChannels = command
                        .getGroupResponses()
                        .stream()
                        .filter(faqChannelGroupCommand -> channelGroupService.isChannelInGroup(channel, faqChannelGroupCommand.getChannelGroup().getChannelGroup()));
                List<FAQChannelGroupCommand> allMatchingCommands = matchingChannels.collect(Collectors.toList());
                if(allMatchingCommands.size() > 1) {
                    log.warn("There are multiple matching channel groups for command {} and channel {}.", command.getId(), channel.getId());
                }
                if(!allMatchingCommands.isEmpty()) {
                    FAQChannelGroupCommand foundCommandChannelGroup = allMatchingCommands.get(0);
                    log.info("Using response from channel group {} for command {} in server {}.", foundCommandChannelGroup.getChannelGroup().getId(), command.getId(), server.getId());
                    groupCommand = Optional.of(foundCommandChannelGroup);
                } else {
                    groupCommand = Optional.empty();
                }
            }
            if(groupCommand.isPresent()) {
                FAQChannelGroupCommand channelGroupCommand = groupCommand.get();
                if(featureModeService.featureModeActive(FAQFeatureDefinition.FAQ, server.getId(), FAQFeatureMode.FAQ_USES)){
                    log.debug("Incrementing use counter for channel group {} for command {} in server {}.", channelGroupCommand.getChannelGroup().getId(), command.getId(), server.getId());
                    channelGroupCommand.setUses(channelGroupCommand.getUses() + 1);
                }
                Set<Long> userIds = channelGroupCommand
                        .getResponses()
                        .stream()
                        .map(FAQCommandResponse::getAuthorUserId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                Long commandId = channelGroupCommand.getGroupCommandId().getCommandId();
                Long channelGroupId = channelGroupCommand.getGroupCommandId().getChannelGroupId();
                CompletableFuture<FAQResponseModel> modelFuture = new CompletableFuture<>();
                if(userIds.isEmpty()) {
                    log.info("No user could not be found for channel group {} for command {} in server {}. Continuing with the bot as user.", channelGroupCommand.getChannelGroup().getId(), command.getId(), server.getId());
                    self.createModel(commandId, channelGroupId, null, modelFuture);
                } else {
                    CompletableFutureList<User> userFutures = userService.retrieveUsers(new ArrayList<>(userIds));
                    userFutures
                            .getMainFuture()
                            .whenComplete((unused, throwable) -> self.createModel(commandId, channelGroupId, userFutures, modelFuture));
                }
                return modelFuture;
            } else {
                throw new NoFAQResponseFoundException();
            }
        }
    }

    public FAQResponseModel getNoCommandFoundModel(AServer server, AChannel channel) {
        List<AChannelGroup> channelGroups = channelGroupService.getChannelGroupsOfChannelWithType(channel, FAQServiceBean.FAQ_CHANNEL_GROUP_KEY);
        List<FAQChannelGroupCommand> possibleCommands = faqChannelGroupCommandManagementServiceBean.getGroupCommandsForBasicChannelGroups(channelGroups);
        List<FAQCommand> globalCommands = faqCommandManagementServiceBean.findGlobalCommandsInServer(server);
        Set<String> commandNames = possibleCommands
                .stream()
                .map(faqChannelGroupCommand -> faqChannelGroupCommand.getCommand().getName())
                .collect(Collectors.toSet());
        commandNames.addAll(globalCommands.stream().map(FAQCommand::getName).collect(Collectors.toSet()));
        return FAQResponseModel
                .builder()
                .availableCommands(new ArrayList<>(commandNames))
                .build();
    }

    @Transactional
    public void createModel(Long commandId, Long channelGroupId, CompletableFutureList<User> userFutures, CompletableFuture<FAQResponseModel> modelFuture) {
        try {
            FAQChannelGroupCommand loadedGroupCommand = faqChannelGroupCommandManagementServiceBean.findChannelGroupCommand(commandId, channelGroupId);
            FAQResponseModel mainModel = FAQResponseModel.builder().build();
            SelfUser selfUser = userService.getSelfUser();
            if(userFutures != null)  {
                List<User> users = userFutures.getObjects();
                Map<Long, User> userMap = users.stream().collect(Collectors.toMap(ISnowflake::getIdLong, Function.identity()));
                loadedGroupCommand.getResponses().forEach(response -> {
                    User user = userMap.getOrDefault(response.getAuthorUserId(), selfUser);
                    mainModel.getMessages().add(FAQResponseMessageModel.fromFAQCommandResponse(response, user));
                });
            } else {
                loadedGroupCommand
                        .getResponses()
                        .forEach(response -> mainModel.getMessages().add(FAQResponseMessageModel.fromFAQCommandResponse(response, selfUser)));
            }
            modelFuture.complete(mainModel);
        } catch (Exception exception) {
            modelFuture.completeExceptionally(exception);
        }
    }
}
