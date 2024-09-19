package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.config.QualityConfig;
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
import java.util.Optional;

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
            modifyEffect(originalData.getFirst(), quality, blacklist).ifPresent(instance -> {
                MobEffect effect = instance.getEffect();
                float probability = originalData.getSecond();

                if (!blacklist.contains(effect)) {
                    if (effect.isBeneficial()) {
                        probability = probability + QualityConfig.getProbabilityAddition(quality);
                    } else if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                        probability = probability - QualityConfig.getProbabilityAddition(quality);
                    }
                }

                if (probability > 0) {
                    builder.effect(() -> instance, Mth.clamp(probability, 0, 1));
                }
            });
        });

        List<Pair<Double, MobEffectInstance>> effects = FoodUtils.getEffects(stack);
        effects.forEach(data -> builder.effect(data::getSecond, data.getFirst().floatValue()));

        return builder.build();
    }

    public static Optional<MobEffectInstance> modifyEffect(final MobEffectInstance instance, final Quality quality) {
        ITagManager<MobEffect> tagManager = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags());
        ITag<MobEffect> blacklist = tagManager.getTag(QFEffectTags.BLACKLIST);
        return modifyEffect(instance, quality, blacklist);
    }

    public static Optional<MobEffectInstance> modifyEffect(final MobEffectInstance instance, final Quality quality, final ITag<MobEffect> blacklist) {
        if (!QualityUtils.isValidQuality(quality)) {
            return Optional.of(instance);
        }

        MobEffect effect = instance.getEffect();

        int duration = instance.getDuration();
        int amplifier = instance.getAmplifier();

        if (blacklist.isEmpty() || !blacklist.contains(effect)) {
            if (effect.isBeneficial()) {
                duration = (int) (duration * QualityConfig.getDurationMultiplier(quality));
                amplifier = amplifier + QualityConfig.getAmplifierAddition(quality);
            } else if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                duration = (int) (duration / QualityConfig.getDurationMultiplier(quality));
                amplifier = amplifier - QualityConfig.getAmplifierAddition(quality);
            }

            if (amplifier >= 0 && duration > 0) {
                return Optional.of(new MobEffectInstance(effect, duration, Math.min(255, amplifier)));
            }

            return Optional.empty();
        }

        return Optional.of(instance);
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
