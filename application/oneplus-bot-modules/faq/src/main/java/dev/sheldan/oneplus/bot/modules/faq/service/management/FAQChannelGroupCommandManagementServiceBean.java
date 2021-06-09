package dev.sheldan.oneplus.bot.modules.faq.service.management;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroup;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQChannelGroupCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommand;
import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.ChannelGroupCommandId;
import dev.sheldan.oneplus.bot.modules.faq.repository.FAQChannelGroupCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FAQChannelGroupCommandManagementServiceBean {

    @Autowired
    private FAQChannelGroupCommandRepository repository;

    @Autowired
    private FAQCommandResponseManagementServiceBean faqCommandResponseManagementServiceBean;

    public FAQChannelGroupCommand createFAQChannelGroupCommand(FAQCommand command, FAQChannelGroup channelGroup) {
        FAQChannelGroupCommand groupCommand = FAQChannelGroupCommand
                .builder()
                .command(command)
                .uses(0)
                .channelGroup(channelGroup)
                .groupCommandId(new ChannelGroupCommandId(channelGroup.getId(), command.getId()))
                .build();
        command.getGroupResponses().add(groupCommand);
        log.info("Creating FAQ channel group command for command {} in channel group {}.", command.getId(), channelGroup.getId());
        return repository.save(groupCommand);
    }

    public FAQChannelGroupCommand findChannelGroupCommand(Long commandId, Long channelGroupId) {
        ChannelGroupCommandId id = ChannelGroupCommandId
                .builder()
                .commandId(commandId)
                .channelGroupId(channelGroupId)
                .build();
        return repository.findById(id)
                .orElseThrow(() -> new AbstractoRunTimeException("Channel group command not found."));
    }

    public void deleteFAQChannelGroupCommands(List<FAQChannelGroupCommand> faqChannelGroupCommandList) {
        log.info("Deleting {} faq channel group commands.", faqChannelGroupCommandList.size());
        faqChannelGroupCommandList
                .forEach(faqChannelGroupCommand -> faqCommandResponseManagementServiceBean.deleteCommandResponses(faqChannelGroupCommand.getResponses()));
        repository.deleteAll(faqChannelGroupCommandList);
    }

    public void deleteFAQChannelGroupCommand(FAQCommand command, FAQChannelGroup channelGroup) {
        FAQChannelGroupCommand channelGroupCommand = findChannelGroupCommand(command.getId(), channelGroup.getId());
        faqCommandResponseManagementServiceBean.deleteCommandResponses(channelGroupCommand.getResponses());
        log.info("Deleting faq channel group command for command {} in channel group {}.", command.getId(), channelGroup.getId());
        repository.delete(channelGroupCommand);
    }

    public List<FAQChannelGroupCommand> getGroupCommandsForBasicChannelGroups(List<AChannelGroup> channelGroups) {
        List<Long> channelGroupIds = channelGroups
                .stream()
                .map(AChannelGroup::getId)
                .collect(Collectors.toList());

        return getGroupCommandsForChannelGroupIds(channelGroupIds);
    }

    public List<FAQChannelGroupCommand> getGroupCommandsForChannelGroupIds(List<Long> channelGroupIds) {
        return repository.findByChannelGroup_IdIn(channelGroupIds);
    }

}
