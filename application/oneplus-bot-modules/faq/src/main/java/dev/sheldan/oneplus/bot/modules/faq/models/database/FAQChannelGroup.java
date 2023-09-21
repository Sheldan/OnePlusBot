package dev.sheldan.oneplus.bot.modules.faq.models.database;

import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import lombok.*;

import jakarta.persistence.*;
import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq_channel_group")
@Getter
@Setter
@EqualsAndHashCode
public class FAQChannelGroup {
    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @PrimaryKeyJoinColumn
    private AChannelGroup channelGroup;

    @Column(name = "global")
    private Boolean global;

    @Column(name = "created", nullable = false, insertable = false, updatable = false)
    private Instant created;

    @Column(name = "updated", insertable = false, updatable = false)
    private Instant updated;
}
