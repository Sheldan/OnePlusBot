package dev.sheldan.oneplus.bot.modules.faq.models.database;

import dev.sheldan.oneplus.bot.modules.faq.models.database.embed.CommandResponseId;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq_command_response")
@Getter
@Setter
@EqualsAndHashCode
public class FAQCommandResponse {
    @EmbeddedId
    private CommandResponseId id;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "additional_message", length = 2000)
    private String additionalMessage;

    @Column(name = "image_url", length = 2000)
    private String imageURL;

    @Column(name = "red")
    private Integer red;

    @Column(name = "green")
    private Integer green;

    @Column(name = "blue")
    private Integer blue;

    @Column(name = "author_user_id")
    private Long authorUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
            {
                    @JoinColumn(updatable = false, insertable = false, name = "channel_group_id", referencedColumnName = "channel_group_id"),
                    @JoinColumn(updatable = false, insertable = false, name = "command_id", referencedColumnName = "command_id")
            })
    private FAQChannelGroupCommand groupCommand;

    @Column(name = "created", nullable = false, insertable = false, updatable = false)
    private Instant created;

    @Column(name = "updated", insertable = false, updatable = false)
    private Instant updated;
}
