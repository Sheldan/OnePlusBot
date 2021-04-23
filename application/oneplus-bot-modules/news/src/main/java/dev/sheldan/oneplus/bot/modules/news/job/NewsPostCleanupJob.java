package dev.sheldan.oneplus.bot.modules.news.job;

import dev.sheldan.oneplus.bot.modules.news.service.NewsServiceBean;
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
public class NewsPostCleanupJob extends QuartzJobBean {

    @Autowired
    private NewsServiceBean newsServiceBean;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("Executing news post cleanup job.");
            newsServiceBean.cleanUpNewsPosts();
        } catch (Exception exception) {
            log.error("Failed to execute news post cleanup job.", exception);
        }
    }
}
