package dev.sheldan.oneplus.bot.modules.faq.models.database;

import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.ChannelGroupCommandId;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq_channel_group_command")
@EqualsAndHashCode
@Getter
@Setter
public class FAQChannelGroupCommand implements Serializable {

    @EmbeddedId
    private ChannelGroupCommandId groupCommandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelGroupId")
    @JoinColumn(name = "channel_group_id", referencedColumnName = "id", nullable = false)
    private FAQChannelGroup channelGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commandId")
    @JoinColumn(name = "command_id", referencedColumnName = "id", nullable = false)
    private FAQCommand command;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupCommand")
    @Builder.Default
    private List<FAQCommandResponse> responses = new ArrayList<>();

    @Column(name = "created", nullable = false, insertable = false, updatable = false)
    private Instant created;

    @Column(name = "uses")
    private Integer uses;
}
