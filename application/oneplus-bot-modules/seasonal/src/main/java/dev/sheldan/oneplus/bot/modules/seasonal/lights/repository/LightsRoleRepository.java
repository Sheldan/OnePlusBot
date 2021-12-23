package dev.sheldan.oneplus.bot.modules.seasonal.lights.repository;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.models.database.LightsRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LightsRoleRepository extends JpaRepository<LightsRole, Long> {
    List<LightsRole> findByServer(AServer server);
}
