package dev.sheldan.oneplus.bot.modules.referral.service;

import dev.sheldan.abstracto.core.models.ServerUser;
import dev.sheldan.oneplus.bot.modules.referral.model.ReferralType;
import dev.sheldan.oneplus.bot.modules.referral.model.database.ReferralUserInAServer;
import dev.sheldan.oneplus.bot.modules.referral.model.template.Referral;
import dev.sheldan.oneplus.bot.modules.referral.service.management.ReferralUserManagementServiceBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReferralServiceBean {

    @Value("${oneplus.bot.referral.referralRepostDays}")
    private Long repostDurationDays;

    @Autowired
    private ReferralUserManagementServiceBean referralUserManagementServiceBean;

    private final Pattern referralPattern = Pattern.compile("(?<whole>https?://(?:www\\.)?oneplus\\.(?:[a-z]{1,63})[^\\s]*invite(?:#(?<identifier>[^\\s]+)|.+=([^\\s&]+)))");
    
    public List<Referral> getReferralsFromMessage(Message message) {
        List<Referral> referrals = new ArrayList<>();
        Matcher matcher = referralPattern.matcher(message.getContentRaw());
        while(matcher.find()) {
            String fullUrl = matcher.group("whole");
            String referralIdentifier = matcher.group("identifier");
            Referral referral = Referral
                    .builder()
                    .referralLink(fullUrl)
                    .type(getType(referralIdentifier, fullUrl))
                    .referralIdentifier(referralIdentifier)
                    .build();
            referrals.add(referral);
        }
        return referrals;
    }

    private ReferralType getType(String identifier, String fullUrl) {
        if(identifier.length() < 20) {
            return ReferralType.SMARTPHONE;
        } else if(fullUrl.contains(".in")) {
            return ReferralType.SMARTPHONE_INDIA;
        } else {
            return ReferralType.ACCESSORIES;
        }
    }

    public void updateDbState(ServerUser serverUser) {
        Optional<ReferralUserInAServer> userOptional = referralUserManagementServiceBean.getReferralFromDb(serverUser);
        if(userOptional.isPresent()) {
            userOptional.get().setLastReferralPost(Instant.now());
        } else {
            referralUserManagementServiceBean.createReferralUser(serverUser);
        }
    }

    public Instant getNextReferralDate(Member member) {
        ServerUser serverUser = ServerUser.fromMember(member);
        Optional<ReferralUserInAServer> userOptional = referralUserManagementServiceBean.getReferralFromDb(serverUser);
        return userOptional.map(referralUserInAServer -> referralUserInAServer
                .getLastReferralPost()
                .plus(repostDurationDays, ChronoUnit.DAYS))
                .orElse(Instant.now());
    }
}
