package dev.sheldan.oneplus.bot.modules.referral.model.template;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ReferralTooRecentModel {
    private Instant nextReferralDate;
}
