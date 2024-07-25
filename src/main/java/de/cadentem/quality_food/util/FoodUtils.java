package de.cadentem.quality_food.util;

import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFEffectTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
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

        int nutrition = (int) (original.nutrition() * type.nutritionMultiplier());
        float saturationModifier = (float) (original.saturation() * type.saturationMultiplier());

        FoodProperties.Builder builder = new FoodProperties.Builder();
        builder.nutrition(nutrition);
        builder.saturationModifier(saturationModifier);

        if (original.canAlwaysEat()) builder.alwaysEdible();
        if (original.eatSeconds() == 0.8f) builder.fast();

        List<FoodProperties.PossibleEffect> originalEffects = original.effects();

        Optional<HolderSet.Named<MobEffect>> tag = BuiltInRegistries.MOB_EFFECT.getTag(QFEffectTags.BLACKLIST);

        originalEffects.forEach(originalData -> {
            MobEffectInstance originalInstance = originalData.effect();
            Holder<MobEffect> effect = originalInstance.getEffect();

            int duration = originalInstance.getDuration();
            int amplifier = originalInstance.getAmplifier();
            float probability = originalData.probability();

            if (tag.isEmpty() || tag.get().contains(effect) ) {
                if (effect.value().isBeneficial()) {
                    duration = (int) (duration * type.durationMultiplier());
                    amplifier = amplifier + type.amplifierModifier();
                    probability = (float) (probability * type.probabilityMultiplier());
                } else if (effect.value().getCategory() == MobEffectCategory.HARMFUL) {
                    if (type.durationMultiplier() == 0) {
                        duration = 0;
                    } else {
                        duration = (int) (duration / type.durationMultiplier());
                    }

                    amplifier = amplifier - type.amplifierModifier();

                    if (type.probabilityMultiplier() == 0) {
                        probability = 0;
                    } else {
                        probability = (float) (probability / type.probabilityMultiplier());
                    }
                }
            }

            if (amplifier >= 0 && duration > 0 && probability > 0) {
                int finalDuration = duration;
                int finalAmplifier = Math.min(255, amplifier);
                builder.effect(() -> new MobEffectInstance(effect, finalDuration, finalAmplifier), Mth.clamp(probability, 0, 1));
            }
        });

        Optional<List<FoodProperties.PossibleEffect>> effects = FoodUtils.getEffects(stack);
        effects.ifPresent(entries -> entries.forEach(entry -> builder.effect(entry::effect, entry.probability())));

        return builder.build();
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
}
