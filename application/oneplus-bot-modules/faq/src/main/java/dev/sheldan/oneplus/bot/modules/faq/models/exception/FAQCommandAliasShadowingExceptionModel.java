package dev.sheldan.oneplus.bot.modules.faq.models.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FAQCommandAliasShadowingExceptionModel {
    private String commandName;
    private String alias;
}
