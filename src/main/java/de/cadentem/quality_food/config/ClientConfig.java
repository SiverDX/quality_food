package de.cadentem.quality_food.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue PARTICLES;
    public static final ModConfigSpec.BooleanValue EFFECT_TOOLTIPS;

    static {
        PARTICLES = BUILDER.comment("Enable / Disable particles which indicate the stored quality level of certain blocks (e.g. furnace)").define("particles", true);
        EFFECT_TOOLTIPS = BUILDER.comment("Enable / Disable effect tooltips for food items").define("food_effects", true);

        SPEC = BUILDER.build();
    }
}
