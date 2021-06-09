package dev.sheldan.oneplus.bot.modules.faq.exception;

import dev.sheldan.abstracto.core.exception.AbstractoTemplatableException;

public class NoFAQResponseFoundException extends AbstractoTemplatableException {

    public NoFAQResponseFoundException() {
        super("No FAQ Command response found.");
    }

    @Override
    public String getTemplateName() {
        return "no_faq_response_found_exception";
    }

    @Override
    public Object getTemplateModel() {
        return new Object();
    }
}
