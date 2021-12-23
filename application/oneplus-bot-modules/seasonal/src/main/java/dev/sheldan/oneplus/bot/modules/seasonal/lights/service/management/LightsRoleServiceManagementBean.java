package dev.sheldan.oneplus.bot.modules.seasonal.lights.service.management;

import dev.sheldan.abstracto.core.models.database.ARole;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.models.database.LightsRole;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.repository.LightsRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LightsRoleServiceManagementBean {

    @Autowired
    private LightsRoleRepository lightMemberRepository;

    public void addMemberToLights(ARole aRole) {
        if(!lightMemberRepository.existsById(aRole.getId())) {
            LightsRole member = LightsRole
                    .builder()
                    .id(aRole.getId())
                    .server(aRole.getServer())
                    .role(aRole)
                    .build();
            lightMemberRepository.save(member);
        }
    }

    public void removeMemberFromLights(ARole aRole) {
        lightMemberRepository.deleteById(aRole.getId());
    }

    public List<LightsRole> getLightsUserInServer(AServer server) {
        return lightMemberRepository.findByServer(server);
    }
}
