package dev.sheldan.oneplus.bot.modules.faq.models.database.embed;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponseId implements Serializable {
    @Column(name = "channel_group_id")
    private Long channelGroupId;
    @Column(name = "command_id")
    private Long commandId;
    @Column(name = "position")
    private Integer position;
}
