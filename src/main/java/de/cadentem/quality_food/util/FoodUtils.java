package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFEffectTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodUtils {
    public static @Nullable FoodProperties handleFoodProperties(final ItemStack stack, @Nullable final FoodProperties original) {
        if (original == null || !QualityUtils.hasQuality(stack)) {
            return original;
        }

        Quality quality = QualityUtils.getQuality(stack);
        int nutrition = (int) (original.getNutrition() * QualityConfig.getNutritionMultiplier(quality));
        float saturationModifier = original.getSaturationModifier() * QualityConfig.getSaturationMultiplier(quality);

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
                    duration = (int) (duration * QualityConfig.getDurationMultiplier(quality));
                    amplifier = amplifier + QualityConfig.getAmplifierAddition(quality);
                    probability = probability + QualityConfig.getProbabilityAddition(quality);
                } else if (originalEffect.getCategory() == MobEffectCategory.HARMFUL) {
                    duration = (int) (duration / QualityConfig.getDurationMultiplier(quality));
                    amplifier = amplifier - QualityConfig.getAmplifierAddition(quality);
                    probability = probability - QualityConfig.getProbabilityAddition(quality);
                }
            }

            if (amplifier >= 0 && duration > 0) {
                int finalDuration = duration;
                int finalAmplifier = Math.min(255, amplifier);
                builder.effect(() -> new MobEffectInstance(originalEffect, finalDuration, finalAmplifier), Mth.clamp(probability, 0, 1));
            }
        });

        List<Pair<Double, MobEffectInstance>> effects = FoodUtils.getEffects(stack);
        effects.forEach(data -> builder.effect(data::getSecond, data.getFirst().floatValue()));

        return builder.build();
    }

    public static List<Pair<Double, MobEffectInstance>> getEffects(@Nullable final ItemStack stack) {
        if (stack == null) {
            return List.of();
        }

        List<Pair<Double, MobEffectInstance>> effects = new ArrayList<>();
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            ListTag effectList = tag.getCompound(QualityUtils.QUALITY_TAG).getList(QualityUtils.EFFECT_TAG, ListTag.TAG_COMPOUND);

            for (int i = 0; i < effectList.size(); i++) {
                CompoundTag effectTag = effectList.getCompound(i);
                double probability = effectTag.getDouble(QualityUtils.EFFECT_PROBABILITY_KEY);
                MobEffectInstance effect = MobEffectInstance.load(effectTag);

                effects.add(Pair.of(probability, effect));
            }
        }

        return effects;
    }
}
