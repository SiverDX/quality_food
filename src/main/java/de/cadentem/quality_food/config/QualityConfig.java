package de.cadentem.quality_food.config;

import de.cadentem.quality_food.core.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class QualityConfig {
    public ForgeConfigSpec.DoubleValue chance;

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
