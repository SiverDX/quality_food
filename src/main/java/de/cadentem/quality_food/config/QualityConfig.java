package de.cadentem.quality_food.config;

import de.cadentem.quality_food.core.Quality;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QualityConfig {
    private final List<EffectConfig> effects = new ArrayList<>();

    public ForgeConfigSpec.DoubleValue chance;

    public ForgeConfigSpec.DoubleValue durationMultiplier;
    public ForgeConfigSpec.DoubleValue probabilityAddition;
    public ForgeConfigSpec.IntValue amplifierAddition;

    public ForgeConfigSpec.DoubleValue nutritionMultiplier;
    public ForgeConfigSpec.DoubleValue saturationMultiplier;

    public ForgeConfigSpec.DoubleValue craftingBonus;

    public ForgeConfigSpec.ConfigValue<List<? extends String>> effect_list_internal;

    public static float getChance(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.chance.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 0.10f;
            case GOLD -> 0.03f;
            case DIAMOND -> 0.005f;
            default -> 0;
        };
    }

    public static double getDurationMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.durationMultiplier.get();
        }

        return 1 + quality.level() * 0.5;
    }

    public static float getProbabilityAddition(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.probabilityAddition.get().floatValue();
        }

        if (quality == Quality.NONE || quality == Quality.NONE_PLAYER_PLACED) {
            return 0;
        }

        return quality.level() / 10f;
    }

    public static int getAmplifierAddition(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.amplifierAddition.get();
        }

        return quality.level();
    }

    public static double getNutritionMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.nutritionMultiplier.get();
        }

        return 1 + quality.level() * 0.5;
    }

    public static float getSaturationMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.saturationMultiplier.get().floatValue();
        }

        return 1 + quality.level() * 0.25f;
    }

    public static float getCraftingBonus(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.craftingBonus.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 0.15f;
            case GOLD -> 0.4f;
            case DIAMOND -> 0.7f;
            default -> 0;
        };
    }

    public void initializeEffects() {
        effects.clear();
        effect_list_internal.get().forEach(effect -> {
            EffectConfig config = EffectConfig.create(effect);

            if (config != null) {
                effects.add(config);
            }
        });
    }

    public List<EffectConfig> getEffects() {
        return effects;
    }
}
