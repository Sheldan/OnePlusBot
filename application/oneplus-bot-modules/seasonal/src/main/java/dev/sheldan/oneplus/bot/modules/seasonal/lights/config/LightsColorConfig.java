package dev.sheldan.oneplus.bot.modules.seasonal.lights.config;

import dev.sheldan.oneplus.bot.modules.seasonal.lights.models.LightsRoleColor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "abstracto.feature.lights")
public class LightsColorConfig {
    private List<LightsRoleColor> colors;
}
