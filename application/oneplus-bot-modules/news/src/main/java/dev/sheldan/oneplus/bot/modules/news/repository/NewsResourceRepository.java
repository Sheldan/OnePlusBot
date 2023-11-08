package dev.sheldan.oneplus.bot.modules.news.repository;

import dev.sheldan.oneplus.bot.modules.news.model.database.NewsSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsResourceRepository extends JpaRepository<NewsSource, Long> {
}
