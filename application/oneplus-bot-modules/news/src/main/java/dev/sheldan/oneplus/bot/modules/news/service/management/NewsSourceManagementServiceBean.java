package dev.sheldan.oneplus.bot.modules.news.service.management;

import dev.sheldan.oneplus.bot.modules.news.model.database.NewsSource;
import dev.sheldan.oneplus.bot.modules.news.repository.NewsResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsSourceManagementServiceBean {

    @Autowired
    private NewsResourceRepository newsResourceRepository;

    public List<NewsSource> loadNewsSources() {
        return newsResourceRepository.findAll();
    }

}
