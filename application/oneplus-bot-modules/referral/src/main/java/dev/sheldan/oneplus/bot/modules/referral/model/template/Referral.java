package dev.sheldan.oneplus.bot.modules.referral.model.template;

import dev.sheldan.oneplus.bot.modules.referral.model.ReferralType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Referral {
    private String referralLink;
    private String referralIdentifier;
    private ReferralType type;
}
