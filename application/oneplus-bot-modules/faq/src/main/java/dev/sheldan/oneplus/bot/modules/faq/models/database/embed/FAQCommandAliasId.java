package dev.sheldan.oneplus.bot.modules.faq.models.database.embed;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQCommandAliasId implements Serializable {
    @Column(name = "command_id")
    private Long faqCommandId;
    @Column(name = "alias", length = 20)
    private String alias;
}
