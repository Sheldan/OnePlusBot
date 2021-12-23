package dev.sheldan.oneplus.bot.modules.seasonal.lights.models;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class LightsRoleColor {
    private Integer r;
    private Integer g;
    private Integer b;

    public Color toColor() {
        return new Color(r, g, b);
    }
}
