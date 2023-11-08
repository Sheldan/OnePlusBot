package dev.sheldan.oneplus.bot.modules.news.job;

import dev.sheldan.oneplus.bot.modules.news.service.NewsSourceServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@DisallowConcurrentExecution
@Component
@PersistJobDataAfterExecution
public class CheckForNewsPosts extends QuartzJobBean {

    @Autowired
    private NewsSourceServiceBean newsSourceServiceBean;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("Executing news post check job.");
            newsSourceServiceBean.checkForNewThreads();
        } catch (Exception exception) {
            log.error("Failed to execute news post check job.", exception);
        }
    }
}
