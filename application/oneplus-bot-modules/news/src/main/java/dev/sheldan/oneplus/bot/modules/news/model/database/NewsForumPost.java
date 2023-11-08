package dev.sheldan.oneplus.bot.modules.news.model.database;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news_forum_post")
@Getter
@Setter
@EqualsAndHashCode
public class NewsForumPost {
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private NewsSource creator;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;
}
