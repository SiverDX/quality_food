package de.cadentem.quality_food.config;

import de.cadentem.quality_food.core.Quality;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static Map<Integer, QualityConfig> QUALITY_CONFIG = new HashMap<>();

    public static ForgeConfigSpec.BooleanValue ENABLE_FALLBACK;

    static {
        ENABLE_FALLBACK = BUILDER.comment("Enable the fallback logic to apply quality to crafted items (this may lead to the quality of other input not being considered)").define("enable_fallback", false);

        for (Quality quality : Quality.values()) {
            if (quality == Quality.NONE) {
                continue;
            }

            BUILDER.push(quality.name());

            QualityConfig config = new QualityConfig();
            config.chance = BUILDER.comment("The chance for a quality to occur (with no luck or other bonus)").defineInRange("chance", QualityConfig.getDefaultChance(quality), 0, 1);
            config.durationMultiplier = BUILDER.comment("By how much the duration of the effect will get multiplied (beneficial) or divided (harmful) for").defineInRange("duration_multiplier", QualityConfig.getDefaultDurationMultiplier(quality), 1, 100);
            config.probabilityAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the probability (chance for the effect to apply)").defineInRange("probability_addition", QualityConfig.getDefaultProbabilityAddition(quality), 0, 1);
            config.amplifierAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the amplifier (level of the effect)").defineInRange("amplifier_addition", QualityConfig.getDefaultAmplifierAddition(quality), 0, 255);
            config.nutritionMultiplier = BUILDER.comment("By how much the nutrition will get multiplied for").defineInRange("nutrition_multiplier", QualityConfig.getDefaultNutritionMultiplier(quality), 1, 100);
            config.saturationMultiplier = BUILDER.comment("By how much the saturation will get multiplied for").defineInRange("saturation_multiplier", QualityConfig.getDefaultSaturationMultiplier(quality), 1, 100);
            QUALITY_CONFIG.put(quality.ordinal(), config);

            BUILDER.pop();
        }

        SPEC = BUILDER.build();
    }
}
