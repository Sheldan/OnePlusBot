package dev.sheldan.oneplus.bot.modules.faq.models.database;

import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.FAQCommandAliasId;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq_command_alias")
@Getter
@Setter
@EqualsAndHashCode
public class FAQCommandAlias implements Serializable {

    @EmbeddedId
    private FAQCommandAliasId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commandId")
    @JoinColumn(name = "command_id", referencedColumnName = "id", nullable = false)
    private FAQCommand command;

    @Column(name = "created", nullable = false, insertable = false, updatable = false)
    private Instant created;

}
