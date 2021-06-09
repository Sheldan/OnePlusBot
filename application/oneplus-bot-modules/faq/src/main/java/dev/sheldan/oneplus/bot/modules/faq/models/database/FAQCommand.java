package dev.sheldan.oneplus.bot.modules.faq.models.database;

import dev.sheldan.abstracto.core.models.database.AServer;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq_command")
@Getter
@Setter
@EqualsAndHashCode
public class FAQCommand implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "global")
    private Boolean global;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private AServer server;

    @Column(name = "created", nullable = false, insertable = false, updatable = false)
    private Instant created;

    @Column(name = "updated", insertable = false, updatable = false)
    private Instant updated;

    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "command")
    @Builder.Default
    private List<FAQChannelGroupCommand> groupResponses = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "command")
    @Builder.Default
    private List<FAQCommandAlias> aliases = new ArrayList<>();
}
