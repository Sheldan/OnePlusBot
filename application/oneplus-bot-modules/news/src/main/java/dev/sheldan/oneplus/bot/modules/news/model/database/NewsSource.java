package dev.sheldan.oneplus.bot.modules.news.model.database;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news_source")
@Getter
@Setter
@EqualsAndHashCode
public class NewsSource {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "thread_count")
    private Long threadCount;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;
}
