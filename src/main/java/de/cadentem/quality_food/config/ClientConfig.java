package de.cadentem.quality_food.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue PARTICLES;
    public static ForgeConfigSpec.BooleanValue EFFECT_TOOLTIPS;

    static {
        PARTICLES = BUILDER.comment("Enable / disable particles to indicate the quality level of certain blocks (e.g. furnace)").define("particles", true);
        EFFECT_TOOLTIPS = BUILDER.comment("Enable / disable effect tooltips for food items").define("food_effects", true);

        SPEC = BUILDER.build();
    }
}
