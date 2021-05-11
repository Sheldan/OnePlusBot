package dev.sheldan.oneplus.bot.modules.referral.model;

import lombok.Getter;

@Getter
public enum ReferralType {
    SMARTPHONE("smartphone"), ACCESSORIES("accessories"), SMARTPHONE_INDIA("smartphoneIndia");

    private String key;

    ReferralType(String key) {
        this.key = key;
    }
}
