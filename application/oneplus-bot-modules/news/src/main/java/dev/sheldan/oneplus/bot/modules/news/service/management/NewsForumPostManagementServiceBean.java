package dev.sheldan.oneplus.bot.modules.news.service.management;

import dev.sheldan.oneplus.bot.modules.news.model.database.NewsForumPost;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsSource;
import dev.sheldan.oneplus.bot.modules.news.repository.NewsForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsForumPostManagementServiceBean {

    @Autowired
    private NewsForumPostRepository repository;

    public List<NewsForumPost> getAllPosts() {
        return repository.findAll();
    }

    public NewsForumPost createPost(NewsSource creator, Long id) {
        NewsForumPost post = NewsForumPost
                .builder()
                .creator(creator)
                .id(id)
                .build();
        return repository.save(post);
    }

}
