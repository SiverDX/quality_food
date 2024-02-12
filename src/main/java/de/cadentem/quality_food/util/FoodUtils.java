package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
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
        float saturationModifier = (float) (original.getSaturationModifier() * getSaturationMultiplier(quality));

        FoodProperties.Builder builder = new FoodProperties.Builder();
        builder.nutrition(nutrition);
        builder.saturationMod(saturationModifier);
        if (original.isMeat()) builder.meat();
        if (original.canAlwaysEat()) builder.alwaysEat();
        if (original.isFastFood()) builder.fast();

        List<Pair<MobEffectInstance, Float>> effects = original.getEffects();

        ITagManager<MobEffect> tagManager = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags());
        ITag<MobEffect> beneficialBlacklist = tagManager.getTag(QFEffectTags.BENEFICIAL_BLACKLIST);
        ITag<MobEffect> harmfulBlacklist = tagManager.getTag(QFEffectTags.HARMFUL_BLACKLIST);

        effects.forEach(effect -> {
            MobEffectInstance originalInstance = effect.getFirst();
            MobEffect originalEffect = originalInstance.getEffect();

            int duration = originalInstance.getDuration();
            int amplifier = originalInstance.getAmplifier();
            float probability = effect.getSecond();

            if (originalEffect.isBeneficial() && !beneficialBlacklist.contains(originalEffect)) {
                duration = (int) (duration * getDurationMultiplier(quality));
                amplifier = amplifier + quality.ordinal();
                probability = probability + (quality.ordinal() / 10f);
            } else if (originalEffect.getCategory() == MobEffectCategory.HARMFUL && !harmfulBlacklist.contains(originalEffect)) {
                duration = (int) (duration / getDurationMultiplier(quality));
                amplifier = amplifier - quality.ordinal();
                probability = probability - (quality.ordinal() / 10f);
            }

            if (amplifier >= 0 && duration > 0) {
                int finalDuration = duration;
                int finalAmplifier = amplifier;
                builder.effect(() -> new MobEffectInstance(originalEffect, finalDuration, finalAmplifier), Mth.clamp(probability, 0, 1));
            }
        });

        return builder.build();
    }

    public static double getDurationMultiplier(final Quality quality) {
        return switch (quality) {
            case IRON -> 1.5;
            case GOLD -> 2;
            case DIAMOND -> 2.5;
            default -> 1;
        };
    }

    public static double getNutritionMultiplier(final Quality quality) {
        return switch (quality) {
            case IRON -> 1.5;
            case GOLD -> 2;
            case DIAMOND -> 2.5;
            default -> 1;
        };
    }

    public static double getSaturationMultiplier(final Quality quality) {
        return switch (quality) {
            case IRON -> 1.25;
            case GOLD -> 1.5;
            case DIAMOND -> 1.75;
            default -> 1;
        };
    }
}
