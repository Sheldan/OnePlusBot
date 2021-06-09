package dev.sheldan.oneplus.bot.modules.faq.config;

import dev.sheldan.abstracto.core.command.config.ModuleDefinition;
import dev.sheldan.abstracto.core.command.config.ModuleInfo;
import org.springframework.stereotype.Component;

@Component
public class FAQModuleDefinition implements ModuleDefinition {

    public static final String FAQ = "faqModule";

    @Override
    public ModuleInfo getInfo() {
        return ModuleInfo.builder().name(FAQ).templated(true).build();
    }

    @Override
    public String getParentModule() {
        return "default";
    }
}
