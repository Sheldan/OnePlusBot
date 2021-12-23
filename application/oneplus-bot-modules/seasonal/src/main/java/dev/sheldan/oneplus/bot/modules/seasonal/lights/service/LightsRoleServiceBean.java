package dev.sheldan.oneplus.bot.modules.seasonal.lights.service;

import dev.sheldan.abstracto.core.models.database.ARole;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.RoleService;
import dev.sheldan.abstracto.core.service.management.RoleManagementService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.config.LightsColorConfig;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.models.database.LightsRole;
import dev.sheldan.oneplus.bot.modules.seasonal.lights.service.management.LightsRoleServiceManagementBean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LightsRoleServiceBean {

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private LightsRoleServiceManagementBean serviceManagementBean;

    @Autowired
    private RoleManagementService roleManagementService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecureRandom secureRandom;

    @Autowired
    private LightsColorConfig lightsColorConfig;

    public void addMemberToSeasonalLights(Role role) {
        ARole aRole = roleManagementService.findRole(role.getIdLong());
        log.info("Adding {} to lights for guild {}.", role.getId(), role.getGuild().getId());
        serviceManagementBean.addMemberToLights(aRole);
    }

    public void removeMemberFromSeasonalLights(Role role) {
        ARole aRole = roleManagementService.findRole(role.getIdLong());
        log.info("Removing {} from lights for guild {}.", role.getId(), role.getGuild().getId());
        serviceManagementBean.removeMemberFromLights(aRole);
    }

    public CompletableFuture<Void> switchLights(Guild guild) {
        log.info("Switching lights in guild {}", guild.getIdLong());
        AServer server = serverManagementService.loadServer(guild.getIdLong());
        List<LightsRole> lightUsers = serviceManagementBean.getLightsUserInServer(server);
        List<Long> roleIds = lightUsers
                .stream()
                .map(lightsMember -> lightsMember.getRole().getId())
                .distinct()
                .collect(Collectors.toList());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        roleIds.forEach(roleId -> {
            Role foundRole = guild.getRoleById(roleId);
            if(foundRole != null) {
                futures.add(roleService.setRoleColorTo(foundRole, getRandomColor()));
            }
        });
        return FutureUtils.toSingleFutureGeneric(futures);
    }

    private Color getRandomColor() {
        return lightsColorConfig.getColors().get(secureRandom.nextInt(lightsColorConfig.getColors().size())).toColor();
    }
}
