package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFEffectTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FoodUtils {
    public static @NotNull FoodProperties calculateFoodProperties(final ItemStack stack, final FoodProperties original) {
        Quality quality = QualityUtils.getQuality(stack);
        int nutrition = (int) (original.getNutrition() * getNutritionMultiplier(quality));
        float saturationModifier = original.getSaturationModifier() * getSaturationMultiplier(quality);

        FoodProperties.Builder builder = new FoodProperties.Builder();
        builder.nutrition(nutrition);
        builder.saturationMod(saturationModifier);
        if (original.isMeat()) builder.meat();
        if (original.canAlwaysEat()) builder.alwaysEat();
        if (original.isFastFood()) builder.fast();

        List<Pair<MobEffectInstance, Float>> originalEffects = original.getEffects();

        ITagManager<MobEffect> tagManager = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags());
        ITag<MobEffect> blacklist = tagManager.getTag(QFEffectTags.BLACKLIST);

        originalEffects.forEach(originalData -> {
            MobEffectInstance originalInstance = originalData.getFirst();
            MobEffect originalEffect = originalInstance.getEffect();

            int duration = originalInstance.getDuration();
            int amplifier = originalInstance.getAmplifier();
            float probability = originalData.getSecond();

            if (!blacklist.contains(originalEffect)) {
                if (originalEffect.isBeneficial()) {
                    duration = (int) (duration * getDurationMultiplier(quality));
                    amplifier = amplifier + getAmplifierAddition(quality);
                    probability = probability + getProbabilityMultiplier(quality);
                } else if (originalEffect.getCategory() == MobEffectCategory.HARMFUL) {
                    duration = (int) (duration / getDurationMultiplier(quality));
                    amplifier = amplifier - getAmplifierAddition(quality);
                    probability = probability - getProbabilityMultiplier(quality);
                }
            }

            if (amplifier >= 0 && duration > 0) {
                int finalDuration = duration;
                int finalAmplifier = Math.min(255, amplifier);
                builder.effect(() -> new MobEffectInstance(originalEffect, finalDuration, finalAmplifier), Mth.clamp(probability, 0, 1));
            }
        });

        List<Pair<Double, MobEffectInstance>> effects = QualityUtils.getEffects(stack);
        effects.forEach(data -> builder.effect(data::getSecond, data.getFirst().floatValue()));

        return builder.build();
    }

    public static double getDurationMultiplier(final Quality quality) {
        QualityConfig qualityConfig = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());

        if (qualityConfig != null) {
            return qualityConfig.durationMultiplier.get();
        }

        return QualityConfig.getDefaultDurationMultiplier(quality);
    }

    public static float getProbabilityMultiplier(final Quality quality) {
        QualityConfig qualityConfig = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());

        if (qualityConfig != null) {
            return qualityConfig.probabilityAddition.get().floatValue();
        }

        return QualityConfig.getDefaultProbabilityAddition(quality);
    }

    public static int getAmplifierAddition(final Quality quality) {
        QualityConfig qualityConfig = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());

        if (qualityConfig != null) {
            return qualityConfig.amplifierAddition.get();
        }

        return QualityConfig.getDefaultAmplifierAddition(quality);
    }

    public static double getNutritionMultiplier(final Quality quality) {
        QualityConfig qualityConfig = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());

        if (qualityConfig != null) {
            return qualityConfig.nutritionMultiplier.get();
        }

        return QualityConfig.getDefaultNutritionMultiplier(quality);
    }

    public static float getSaturationMultiplier(final Quality quality) {
        QualityConfig qualityConfig = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());

        if (qualityConfig != null) {
            return qualityConfig.saturationMultiplier.get().floatValue();
        }

        return (float) QualityConfig.getDefaultSaturationMultiplier(quality);
    }
}
