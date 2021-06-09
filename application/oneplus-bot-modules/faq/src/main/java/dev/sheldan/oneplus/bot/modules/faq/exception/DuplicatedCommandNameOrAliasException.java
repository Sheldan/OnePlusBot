package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.DuplicatedCommandNameExceptionModel;

import java.util.List;

public class DuplicatedCommandNameOrAliasException extends AbstractoTemplatableException {

    private final DuplicatedCommandNameExceptionModel model;

    public DuplicatedCommandNameOrAliasException(List<String> commandKeys) {
        super("Duplicated command name or alias found.");
        this.model = DuplicatedCommandNameExceptionModel
                .builder()
                .duplicatedCommandKeys(commandKeys)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "duplicated_command_or_alias_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
