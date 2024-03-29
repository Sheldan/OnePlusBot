package dev.sheldan.oneplus.bot.modules.seasonal.config;

import dev.sheldan.abstracto.core.command.config.ModuleDefinition;
import dev.sheldan.abstracto.core.command.config.ModuleInfo;
import org.springframework.stereotype.Component;

@Component
public class SeasonalEntertainmentFeatureDefinition implements ModuleDefinition {
    public static final String ENTERTAINMENT = "entertainment";

    @Override
    public ModuleInfo getInfo() {
        return ModuleInfo
                .builder()
                .name(ENTERTAINMENT)
                .templated(true)
                .build();
    }

    @Override
    public String getParentModule() {
        return "default";
    }
}
