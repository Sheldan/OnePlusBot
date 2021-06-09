package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.FAQCommandResponseDuplicatedPositionExceptionModel;

public class FAQCommandResponseDuplicatedPositionException extends AbstractoTemplatableException {
    private final FAQCommandResponseDuplicatedPositionExceptionModel model;

    public FAQCommandResponseDuplicatedPositionException(String commandName, String channelGroupName) {
        super("Two messages within a FAQ command response have the same position.");
        this.model = FAQCommandResponseDuplicatedPositionExceptionModel
                .builder()
                .channelGroupName(channelGroupName)
                .commandName(commandName)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "faq_command_response_duplicated_position_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
