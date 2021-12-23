package dev.sheldan.oneplus.bot.modules.seasonal.lights.models.database;

import dev.sheldan.abstracto.core.models.database.ARole;
import dev.sheldan.abstracto.core.models.database.AServer;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lights_role")
@Getter
@Setter
@EqualsAndHashCode
public class LightsRole {
    @Id
    @Column(name = "id")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private AServer server;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private ARole role;
}
