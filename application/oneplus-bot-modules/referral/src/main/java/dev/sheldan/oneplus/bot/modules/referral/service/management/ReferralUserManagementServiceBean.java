package dev.sheldan.oneplus.bot.modules.referral.service.management;

import dev.sheldan.abstracto.core.models.ServerUser;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import dev.sheldan.oneplus.bot.modules.referral.model.database.ReferralUserInAServer;
import dev.sheldan.oneplus.bot.modules.referral.repository.ReferralUserInAServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class ReferralUserManagementServiceBean {

    @Autowired
    private ReferralUserInAServerRepository repository;

    @Autowired
    private UserInServerManagementService userInServerManagementService;

    public Optional<ReferralUserInAServer> getReferralFromDb(ServerUser serverUser) {
        return repository.findByServer_IdAndUser_UserReference_Id(serverUser.getServerId(), serverUser.getUserId());
    }

    public ReferralUserInAServer createReferralUser(ServerUser serverUser) {
        AUserInAServer userInAServer = userInServerManagementService.loadOrCreateUser(serverUser);
        ReferralUserInAServer user = ReferralUserInAServer
                .builder()
                .user(userInAServer)
                .id(userInAServer.getUserInServerId())
                .lastReferralPost(Instant.now())
                .server(userInAServer.getServerReference())
                .build();
        return repository.save(user);
    }
}
