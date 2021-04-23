package dev.sheldan.oneplus.bot.modules.news.config;

import dev.sheldan.abstracto.core.command.config.ModuleDefinition;
import dev.sheldan.abstracto.core.command.config.ModuleInfo;
import org.springframework.stereotype.Component;

@Component
public class NewsModuleDefinition implements ModuleDefinition {

    public static final String NEWS = "news";

    @Override
    public ModuleInfo getInfo() {
        return ModuleInfo.builder().name(NEWS).templated(true).build();
    }

    @Override
    public String getParentModule() {
        return "default";
    }
}
