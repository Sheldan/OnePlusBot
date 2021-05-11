package dev.sheldan.oneplus.bot.modules.referral.model.template;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

@Getter
@Setter
@Builder
public class ReferralPostModel {
    private Member postingMember;
    private List<Referral> referrals;
}
