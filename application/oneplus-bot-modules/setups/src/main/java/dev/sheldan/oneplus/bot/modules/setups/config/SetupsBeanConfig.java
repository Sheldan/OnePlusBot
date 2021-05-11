package dev.sheldan.oneplus.bot.modules.setups.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class SetupsBeanConfig {
    @Bean(value = "setupsDelayedExecutor")
    public ScheduledExecutorService getDelayedExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
