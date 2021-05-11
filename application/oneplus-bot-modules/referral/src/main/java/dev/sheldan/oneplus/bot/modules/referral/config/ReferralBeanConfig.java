package dev.sheldan.oneplus.bot.modules.referral.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ReferralBeanConfig {
    @Bean(value = "referralDelayExecutor")
    public ScheduledExecutorService getDelayedExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
