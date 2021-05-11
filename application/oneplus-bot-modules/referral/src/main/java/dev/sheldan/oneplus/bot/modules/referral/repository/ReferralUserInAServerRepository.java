package dev.sheldan.oneplus.bot.modules.referral.repository;

import dev.sheldan.oneplus.bot.modules.referral.model.database.ReferralUserInAServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralUserInAServerRepository extends JpaRepository<ReferralUserInAServer, Long> {
    Optional<ReferralUserInAServer> findByServer_IdAndUser_UserReference_Id(Long serverId, Long userId);
}
