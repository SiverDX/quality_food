package de.cadentem.quality_food.config;

import de.cadentem.quality_food.core.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QualityConfig {
    public ForgeConfigSpec.DoubleValue chance;
    public ForgeConfigSpec.DoubleValue chanceCropAddition;

    public ForgeConfigSpec.DoubleValue durationMultiplier;
    public ForgeConfigSpec.DoubleValue probabilityAddition;
    public ForgeConfigSpec.IntValue amplifierAddition;

    public ForgeConfigSpec.DoubleValue nutritionMultiplier;
    public ForgeConfigSpec.DoubleValue saturationMultiplier;

    public ForgeConfigSpec.ConfigValue<List<? extends String>> effectList;

    private List<Effect> effects;

    public record Effect(MobEffect effect, double chance, int duration, int amplifier, double probability) {}

    public List<Effect> getEffects() {
        if (effects == null) {
            initializeEffects();
        }

        return effects;
    }

    public void initializeEffects() {
        effects = new ArrayList<>();

        for (String effect : effectList.get()) {
            String[] data = effect.split(";");
            ResourceLocation location = new ResourceLocation(data[0]);

            if (!ForgeRegistries.MOB_EFFECTS.containsKey(location)) {
                continue;
            }

            effects.add(new Effect(ForgeRegistries.MOB_EFFECTS.getValue(location), Double.parseDouble(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Double.parseDouble(data[4])));
        }
    }

    public static float getChanceCropAddition(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.chanceCropAddition.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 0.20f;
            case GOLD -> 0.40f;
            case DIAMOND -> 0.60f;
            default -> 0;
        };
    }

    public static float getChance(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.chance.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 0.15f;
            case GOLD -> 0.07f;
            case DIAMOND -> 0.03f;
            default -> 0;
        };
    }

    public static double getDurationMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.durationMultiplier.get();
        }

        return 1 + quality.value() * 0.5;
    }

    public static float getProbabilityAddition(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.probabilityAddition.get().floatValue();
        }

        if (quality == Quality.NONE || quality == Quality.NONE_PLAYER_PLACED) {
            return 0;
        }

        return quality.value() / 10f;
    }

    public static int getAmplifierAddition(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.amplifierAddition.get();
        }

        return quality.value();
    }

    public static double getNutritionMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.nutritionMultiplier.get();
        }

        return 1 + quality.value() * 0.5;
    }

    public static float getSaturationMultiplier(@NotNull final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);

        if (config != null) {
            return config.saturationMultiplier.get().floatValue();
        }

        return 1 + quality.value() * 0.25f;
    }
}
