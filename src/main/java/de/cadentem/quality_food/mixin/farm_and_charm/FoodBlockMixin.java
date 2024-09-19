package de.cadentem.quality_food.mixin.farm_and_charm;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.config.QualityConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.satisfy.farm_and_charm.block.FoodBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Modify food properties */
@Mixin(FoodBlock.class)
public abstract class FoodBlockMixin {
    @ModifyArg(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private int quality_food$modifyNutrition(int nutrition, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (int) (nutrition * QualityConfig.getNutritionMultiplier(LevelData.get(level, position)));
    }

    @ModifyArg(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private float quality_food$modifyNutrition(float saturation, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return saturation * QualityConfig.getSaturationMultiplier(LevelData.get(level, position));
    }
}