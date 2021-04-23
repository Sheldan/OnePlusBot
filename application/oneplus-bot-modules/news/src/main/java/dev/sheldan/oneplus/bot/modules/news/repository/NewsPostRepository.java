package dev.sheldan.oneplus.bot.modules.news.repository;

import dev.sheldan.oneplus.bot.modules.news.model.database.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

    List<NewsPost> findByCreatedLessThanAndLockedFalse(Instant date);
    List<NewsPost> findByUpdatedLessThanAndLockedTrue(Instant date);
    Optional<NewsPost> findByNewsMessageId(Long newsMessageId);
}
