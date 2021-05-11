package dev.sheldan.oneplus.bot.modules.referral.model.database;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "referral_user_in_server")
@Getter
@Setter
@EqualsAndHashCode
public class ReferralUserInAServer {

    @Id
    @Column(name = "id")
    private Long id;

    /**
     * The {@link AUserInAServer user} which is represented by this object
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @PrimaryKeyJoinColumn
    private AUserInAServer user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private AServer server;

    @Column(name = "last_referral_post")
    private Instant lastReferralPost;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

}
