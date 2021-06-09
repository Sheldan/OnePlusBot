package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.GlobalFAQCommandConfigMismatchExceptionModel;

public class GlobalFAQCommandConfigMismatchException extends AbstractoTemplatableException {
    private final GlobalFAQCommandConfigMismatchExceptionModel model;

    public GlobalFAQCommandConfigMismatchException(String faqCommandName) {
        super("The configuration for a global FAQ command does not make sense. It must be a the global FAQ channel group and the command needs to have global enabled.");
        this.model = GlobalFAQCommandConfigMismatchExceptionModel
                .builder()
                .commandName(faqCommandName)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "global_faq_command_config_mismatch_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
