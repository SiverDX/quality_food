package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.core.Quality;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        effects.forEach(effect -> builder.effect(effect::getFirst, effect.getSecond()));

        return builder.build();
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
