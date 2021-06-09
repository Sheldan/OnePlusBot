package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.FAQCommandNotFoundExceptionModel;

public class FAQCommandNotFoundException extends AbstractoTemplatableException {
    private final FAQCommandNotFoundExceptionModel model;

    public FAQCommandNotFoundException(String commandName) {
        super("FAQ command not found.");
        this.model = FAQCommandNotFoundExceptionModel
                .builder()
                .commandName(commandName)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "faq_command_not_found_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
