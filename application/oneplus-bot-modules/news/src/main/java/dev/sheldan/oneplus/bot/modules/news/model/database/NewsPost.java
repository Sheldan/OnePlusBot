package dev.sheldan.oneplus.bot.modules.news.model.database;

import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import lombok.*;

import jakarta.persistence.*;
import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news_post")
@Getter
@Setter
@EqualsAndHashCode
public class NewsPost {
    @Id
    @Column(name = "source_message_id")
    private Long sourceMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private AServer server;

    @Column(name = "news_message_id")
    private Long newsMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_in_server_id", nullable = false)
    private AUserInAServer author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_channel_id", nullable = false)
    private AChannel sourceChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_channel_id", nullable = false)
    private AChannel newsChannel;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;
}
