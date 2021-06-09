package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.GlobalCommandResponseExceptionModel;

import java.util.List;

public class GlobalFAQCommandResponsesException extends AbstractoTemplatableException {
    private final GlobalCommandResponseExceptionModel model;

    public GlobalFAQCommandResponsesException(List<String> commandNames) {
        super("A global command can only have one response.");
        this.model = GlobalCommandResponseExceptionModel
                .builder()
                .commandNames(commandNames)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "global_faq_command_responses_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
