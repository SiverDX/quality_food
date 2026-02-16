package de.cadentem.quality_food.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record QualityType(
        int level,
        double chance,
        double weight,
        double minWeight,
        double durationMultiplier,
        double probabilityMultiplier,
        int amplifierModifier,
        double nutritionMultiplier,
        double saturationMultiplier,
        double craftingBonus,
        double cookingBonus,
        Optional<List<Effect>> effects,
        ResourceLocation icon
) {
    public static final QualityType NONE = new QualityType(
            0,
            0,
            0,
            0,
            1,
            1,
            0,
            1,
            1,
            0,
            0,
            Optional.empty(),
            QualityFood.location("none")
    );

    private static final RandomSource RANDOM = RandomSource.create();

    public static final Codec<QualityType> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("level").forGetter(QualityType::level),
                    Codec.doubleRange(0 ,1).fieldOf("chance").forGetter(QualityType::chance),
                    Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(QualityType::weight),
                    Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_weight").forGetter(QualityType::minWeight),
                    Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("duration_multiplier", 1d).forGetter(QualityType::durationMultiplier),
                    Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("probability_multiplier", 1d).forGetter(QualityType::probabilityMultiplier),
                    Codec.intRange(-255, 255).optionalFieldOf("amplifier_modifier", 0).forGetter(QualityType::amplifierModifier),
                    Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("nutrition_multiplier").forGetter(QualityType::nutritionMultiplier),
                    Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("saturation_multiplier").forGetter(QualityType::saturationMultiplier),
                    Codec.doubleRange(0, 1).optionalFieldOf("crafting_bonus", 0d).forGetter(QualityType::craftingBonus),
                    Codec.doubleRange(0, 1).optionalFieldOf("cooking_bonus", 0d).forGetter(QualityType::cookingBonus),
                    Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("crop_multiplier", 1d).forGetter(QualityType::cropMultiplier),
                    Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("seed_multiplier", 1d).forGetter(QualityType::seedMultiplier),
                    Effect.CODEC.listOf().optionalFieldOf("effects").forGetter(QualityType::effects),
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(QualityType::icon))
            .apply(builder, QualityType::new));

    /**
     * @param stack Used to determine which effects are applicable for this quality
     */
    public static Quality createQuality(final Holder<QualityType> holder, final ItemStack stack) {
        List<FoodProperties.PossibleEffect> effectsToApply = null;
        QualityType type = holder.value();

        if (type.effects().isPresent()) {
            effectsToApply = new ArrayList<>();

            for (Effect configuration : type.effects().get()) {
                if (configuration.test(stack)) {
                    for (ChanceEffect chanceEffect : configuration.effects()) {
                        if (chanceEffect.chance() > 0 && RANDOM.nextDouble() < chanceEffect.chance()) {
                            effectsToApply.add(chanceEffect.effect());
                        }
                    }
                }
            }

            if (effectsToApply.isEmpty()) {
                effectsToApply = null;
            }
        }

        Optional<ResourceKey<QualityType>> optional = holder.unwrapKey();

        if (optional.isPresent()) {
            return new Quality(optional.get().location(), type.level(), Optional.ofNullable(effectsToApply));
        } else {
            return Quality.NONE;
        }
    }
}