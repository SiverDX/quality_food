package de.cadentem.quality_food.config;

import de.cadentem.quality_food.core.Quality;
import net.minecraftforge.common.ForgeConfigSpec;

public class QualityConfig {
    public ForgeConfigSpec.DoubleValue chance;
    public ForgeConfigSpec.DoubleValue durationMultiplier;
    public ForgeConfigSpec.DoubleValue probabilityAddition;
    public ForgeConfigSpec.IntValue amplifierAddition;
    public ForgeConfigSpec.DoubleValue nutritionMultiplier;
    public ForgeConfigSpec.DoubleValue saturationMultiplier;

    public static float getDefaultChance(final Quality quality) {
        return switch (quality) {
            case IRON -> 0.15f;
            case GOLD -> 0.07f;
            case DIAMOND -> 0.03f;
            default -> 0;
        };
    }

    public static double getDefaultDurationMultiplier(final Quality quality) {
        if (quality == Quality.NONE) {
            return 1;
        }

        return 1 + quality.ordinal() * 0.5;
    }

    public static float getDefaultProbabilityAddition(final Quality quality) {
        if (quality == Quality.NONE) {
            return 0;
        }

        return quality.ordinal() / 10f;
    }

    public static int getDefaultAmplifierAddition(final Quality quality) {
        if (quality == Quality.NONE) {
            return 0;
        }

        return quality.ordinal();
    }

    public static double getDefaultNutritionMultiplier(final Quality quality) {
        if (quality == Quality.NONE) {
            return 1;
        }

        return 1 + quality.ordinal() * 0.5;
    }

    public static double getDefaultSaturationMultiplier(final Quality quality) {
        if (quality == Quality.NONE) {
            return 1;
        }

        return 1 + quality.ordinal() * 0.25;
    }
}
