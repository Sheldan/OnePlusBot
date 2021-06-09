package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;
import dev.sheldan.oneplus.bot.modules.faq.models.exception.FAQCommandAliasShadowingExceptionModel;

public class FAQCommandAliasShadowingException extends AbstractoTemplatableException {

    private final FAQCommandAliasShadowingExceptionModel model;

    public FAQCommandAliasShadowingException(String commandName, String alias) {
        super("Alias shadows another existing command.");
        this.model = FAQCommandAliasShadowingExceptionModel
                .builder()
                .alias(alias)
                .commandName(commandName)
                .build();
    }

    @Override
    public String getTemplateName() {
        return "faq_command_alias_shadowing_exception";
    }

    @Override
    public Object getTemplateModel() {
        return this.model;
    }
}
