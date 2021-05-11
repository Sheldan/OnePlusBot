package dev.sheldan.oneplus.bot.modules.setup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class SetupBeanConfig {
    @Bean(value = "setupDelayedExecutor")
    public ScheduledExecutorService getDelayedExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
