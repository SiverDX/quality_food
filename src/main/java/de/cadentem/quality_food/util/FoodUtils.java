package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFEffectTags;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import java.util.Optional;

public class FoodUtils {
    public static @Nullable FoodProperties handleFoodProperties(final ItemStack stack, @Nullable final FoodProperties original) {
        if (original == null || !QualityUtils.hasQuality(stack)) {
            return original;
        }

        Holder<QualityType> holder = QualityUtils.getType(stack);
        QualityType type = holder.value();

        if (type == QualityType.NONE) {
            return original;
        }

        FoodProperties.Builder builder = getBuilder(original, type);
        return builder.build();
    }

    public static @Nullable Consumable handleConsumable(final ItemStack stack, @Nullable final Consumable original) {
        if (original == null || !QualityUtils.hasQuality(stack)) {
            return original;
        }
        QualityType type = QualityUtils.getType(stack).value();
        if (type == QualityType.NONE) {
            return original;
        }

        List<ConsumeEffect> effects = new ArrayList<>();
        Optional<HolderSet.Named<MobEffect>> blacklist = BuiltInRegistries.MOB_EFFECT.get(QFEffectTags.BLACKLIST);
        for (ConsumeEffect effect : original.onConsumeEffects()) {
            if (!(effect instanceof ApplyStatusEffectsConsumeEffect status)) {
                effects.add(effect);
                continue;
            }
            for (MobEffectInstance instance : status.effects()) {
                modifyEffect(instance, type, blacklist).ifPresent(modified -> {
                    float probability = status.probability();
                    if (blacklist.isEmpty() || !blacklist.get().contains(instance.getEffect())) {
                        if (instance.getEffect().value().isBeneficial()) {
                            probability = (float) (probability * type.probabilityMultiplier());
                        } else if (instance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                            probability = type.probabilityMultiplier() == 0 ? 0 : (float) (probability / type.probabilityMultiplier());
                        }
                    }
                    if (probability > 0) {
                        effects.add(new ApplyStatusEffectsConsumeEffect(List.of(modified), Mth.clamp(probability, 0, 1)));
                    }
                });
            }
        }
        getEffects(stack).ifPresent(effects::addAll);
        return new Consumable(original.consumeSeconds(), original.animation(), original.sound(), original.hasConsumeParticles(), effects);
    }

    public static Optional<MobEffectInstance> modifyEffect(final MobEffectInstance instance, final QualityType type) {
        return modifyEffect(instance, type, BuiltInRegistries.MOB_EFFECT.get(QFEffectTags.BLACKLIST));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<MobEffectInstance> modifyEffect(final MobEffectInstance instance, final QualityType type, final Optional<HolderSet.Named<MobEffect>> blacklist) {
        if (type == QualityType.NONE) {
            return Optional.of(instance);
        }

        Holder<MobEffect> effect = instance.getEffect();

        int duration = instance.getDuration();
        int amplifier = instance.getAmplifier();

        if (blacklist.isEmpty() || !blacklist.get().contains(effect)) {
            if (effect.value().isBeneficial()) {
                duration = (int) (duration * type.durationMultiplier());
                amplifier = amplifier + type.amplifierModifier();
            } else if (effect.value().getCategory() == MobEffectCategory.HARMFUL) {
                if (type.durationMultiplier() == 0) {
                    duration = 0;
                } else {
                    duration = (int) (duration / type.durationMultiplier());
                }

                amplifier = amplifier - type.amplifierModifier();
            }


            if (amplifier >= 0 && duration > 0) {
                return Optional.of(new MobEffectInstance(effect, duration, amplifier));
            }

            return Optional.empty();
        }

        return Optional.of(instance);
    }

    public static Optional<List<ConsumeEffect>> getEffects(@Nullable final ItemStack stack) {
        if (stack == null) {
            return Optional.empty();
        }

        Quality quality = stack.get(QFComponents.QUALITY_DATA_COMPONENT);

        if (quality != null) {
            return quality.effects();
        }

        return Optional.empty();
    }

    private static @NotNull FoodProperties.Builder getBuilder(@NotNull final FoodProperties original, final QualityType type) {
        int nutrition = (int) (original.nutrition() * type.nutritionMultiplier());
        float originalSaturationModifier = (original.saturation() / original.nutrition()) / 2;
        float saturationModifier = (float) (originalSaturationModifier * type.saturationMultiplier());

        FoodProperties.Builder builder = new FoodProperties.Builder();
        builder.nutrition(nutrition);
        builder.saturationModifier(saturationModifier);

        if (original.canAlwaysEat()) builder.alwaysEdible();

        return builder;
    }
}
