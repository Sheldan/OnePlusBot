package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.DuplicatedFAQCommandAliasExceptionModel;

public class DuplicatedFAQCommandAliasException extends AbstractoTemplatableException {
    private final DuplicatedFAQCommandAliasExceptionModel model;

    public DuplicatedFAQCommandAliasException(String commandName, String alias, String usedCommand) {
        super("Alias for FAQ command is already used as an alias in another name.");
        this.model = DuplicatedFAQCommandAliasExceptionModel
                .builder()
                .alias(alias)
                .commandName(commandName)
                .originCommandName(usedCommand)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "duplicated_faq_command_alias_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
