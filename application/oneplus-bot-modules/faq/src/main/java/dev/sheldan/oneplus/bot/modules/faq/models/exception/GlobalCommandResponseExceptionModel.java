package dev.sheldan.oneplus.bot.modules.faq.models.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GlobalCommandResponseExceptionModel {
    private List<String> commandNames;
}
