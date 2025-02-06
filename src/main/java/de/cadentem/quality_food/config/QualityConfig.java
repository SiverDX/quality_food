package de.cadentem.quality_food.config;

import de.cadentem.quality_food.core.Quality;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QualityConfig {
    private final List<EffectConfig> effects = new ArrayList<>();

    public ForgeConfigSpec.DoubleValue weight;
    public ForgeConfigSpec.DoubleValue minWeight;

    public ForgeConfigSpec.DoubleValue chance;
    public ForgeConfigSpec.DoubleValue cropMultiplier;
    public ForgeConfigSpec.DoubleValue seedMultiplier;

    public ForgeConfigSpec.DoubleValue durationMultiplier;
    public ForgeConfigSpec.DoubleValue probabilityAddition;
    public ForgeConfigSpec.IntValue amplifierAddition;

    public ForgeConfigSpec.DoubleValue nutritionMultiplier;
    public ForgeConfigSpec.DoubleValue saturationMultiplier;

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

    public static double getWeight(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.weight.get();
        }

        return switch (quality) {
            case IRON -> 1;
            case GOLD -> 2;
            case DIAMOND -> 3;
            default -> 0;
        };
    }

    @SuppressWarnings("DuplicateBranchesInSwitch") // ignore
    public static double getMinWeight(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.minWeight.get();
        }

        return switch (quality) {
            case IRON -> 0;
            case GOLD -> 1;
            case DIAMOND -> 1.75;
            default -> 0;
        };
    }

    public static float getCropMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.cropMultiplier.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 1;
            case GOLD -> 0.9f;
            case DIAMOND -> 0.75f;
            default -> 0;
        };
    }

    public static float getSeedMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.seedMultiplier.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 1;
            case GOLD -> 0.9f;
            case DIAMOND -> 0.75f;
            default -> 0;
        };
    }

    /**
     * A value of '0' means the threshold has not been reached to get that quality <br>
     * A value of '1' means that the quality is guaranteed
     */
    public static double calculateChance(final Quality quality, double averageWeight) {
        double minWeight = getMinWeight(quality);
        return Mth.clamp((  averageWeight - minWeight) / (getWeight(quality) - minWeight), 0, 1);
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
