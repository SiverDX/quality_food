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
import java.util.Optional;

public class FoodUtils {
    public static @Nullable FoodProperties handleFoodProperties(final ItemStack stack, @Nullable final FoodProperties original) {
        if (original == null || !QualityUtils.hasQuality(stack)) {
            return original;
        }

        QualityType type = QualityUtils.getType(stack);

        if (type == QualityType.NONE) {
            return original;
        }

        FoodProperties.Builder builder = getBuilder(original, type);
        List<FoodProperties.PossibleEffect> originalEffects = original.effects();
        Optional<HolderSet.Named<MobEffect>> blacklist = BuiltInRegistries.MOB_EFFECT.getTag(QFEffectTags.BLACKLIST);

        originalEffects.forEach(originalData -> {
            Optional<MobEffectInstance> optional = modifyEffect(originalData.effect(), type, blacklist);

            optional.ifPresent(instance -> {
                float probability = originalData.probability();

                if (blacklist.isEmpty() || !blacklist.get().contains(instance.getEffect()) ) {
                    if (instance.getEffect().value().isBeneficial()) {
                        probability = (float) (probability * type.probabilityMultiplier());
                    } else if (instance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                        if (type.probabilityMultiplier() == 0) {
                            probability = 0;
                        } else {
                            probability = (float) (probability / type.probabilityMultiplier());
                        }
                    }
                }

                if (probability > 0) {
                    builder.effect(() -> instance, Mth.clamp(probability, 0, 1));
                }
            });
        });

        Optional<List<FoodProperties.PossibleEffect>> effects = FoodUtils.getEffects(stack);
        effects.ifPresent(entries -> entries.forEach(entry -> builder.effect(entry::effect, entry.probability())));

        return builder.build();
    }

    public static Optional<MobEffectInstance> modifyEffect(final MobEffectInstance instance, final QualityType type) {
        return modifyEffect(instance, type, BuiltInRegistries.MOB_EFFECT.getTag(QFEffectTags.BLACKLIST));
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

    public static Optional<List<FoodProperties.PossibleEffect>> getEffects(@Nullable final ItemStack stack) {
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
        if (original.eatSeconds() == 0.8f) builder.fast();

        return builder;
    }
}
