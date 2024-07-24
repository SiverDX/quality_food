package de.cadentem.quality_food.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record QualityType(int level, double chance, double durationMultiplier, double probabilityMultiplier, int amplifierModifier, double nutritionMultiplier, double saturationMultiplier, double craftingBonus, double cookingBonus, Optional<List<Effect>> effects, ResourceLocation icon) {
    public static final QualityType NONE = new QualityType(0, 0, 1, 1, 0, 1, 1, 0, 0, Optional.empty(), QualityFood.location("none"));

    private static final RandomSource RANDOM = RandomSource.create();

    public static final Codec<QualityType> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    Codec.INT.fieldOf("level").forGetter(QualityType::level),
                    Codec.DOUBLE.fieldOf("chance").forGetter(QualityType::chance),
                    Codec.DOUBLE.optionalFieldOf("duration_multiplier", 1d).forGetter(QualityType::durationMultiplier),
                    Codec.DOUBLE.optionalFieldOf("probability_multiplier", 1d).forGetter(QualityType::probabilityMultiplier),
                    Codec.INT.optionalFieldOf("amplifier_modifier", 0).forGetter(QualityType::amplifierModifier),
                    Codec.DOUBLE.fieldOf("nutrition_multiplier").forGetter(QualityType::nutritionMultiplier),
                    Codec.DOUBLE.fieldOf("saturation_multiplier").forGetter(QualityType::saturationMultiplier),
                    Codec.DOUBLE.optionalFieldOf("crafting_bonus", 0d).forGetter(QualityType::craftingBonus),
                    Codec.DOUBLE.optionalFieldOf("cooking_bonus", 0d).forGetter(QualityType::cookingBonus),
                    Effect.CODEC.listOf().optionalFieldOf("effects").forGetter(QualityType::effects),
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(QualityType::icon))
            .apply(builder, QualityType::new));

    public QualityType {
        level = Math.max(1, level);
        chance = Math.clamp(chance, 0, 1);

        durationMultiplier = Math.max(0, durationMultiplier);
        probabilityMultiplier = Math.max(0, probabilityMultiplier);
        amplifierModifier = Math.clamp(amplifierModifier, -255, 255);

        nutritionMultiplier = Math.max(0, nutritionMultiplier);
        saturationMultiplier = Math.max(0, saturationMultiplier);

        craftingBonus = Math.clamp(craftingBonus, 0, 1);
        cookingBonus = Math.clamp(cookingBonus, 0, 1);
    }

    public @Nullable Quality createQuality(final ItemStack stack) {
        Registry<QualityType> registry = Utils.getQualityRegistry();

        if (registry == null) {
            return null;
        }

        if (effects.isPresent()) {
            List<FoodProperties.PossibleEffect> effectsToApply = new ArrayList<>();

            for (Effect configuration : effects.get()) {
                if (configuration.test(stack)) {
                    for (ChanceEffect chanceEffect : configuration.effects()) {
                        if (chanceEffect.chance() > 0 && RANDOM.nextDouble() < chanceEffect.chance()) {
                            effectsToApply.add(chanceEffect.effect());
                        }
                    }
                }
            }

            if (!effectsToApply.isEmpty()) {
                return new Quality(registry.getKey(this), level, Optional.of(effectsToApply));
            }
        }

        return new Quality(registry.getKey(this), level, Optional.empty());
    }
}