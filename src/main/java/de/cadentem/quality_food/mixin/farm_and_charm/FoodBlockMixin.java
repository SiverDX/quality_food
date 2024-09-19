package de.cadentem.quality_food.mixin.farm_and_charm;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.satisfy.farm_and_charm.block.FoodBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Modify food properties */
@Mixin(FoodBlock.class)
public abstract class FoodBlockMixin {
    @ModifyArg(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), remap = false)
    private int quality_food$modifyNutrition(int nutrition, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (int) (nutrition * LevelData.get(level, position).getType().nutritionMultiplier());
    }

    @ModifyArg(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), remap = false)
    private float quality_food$modifyNutrition(float saturation, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (float) (saturation * LevelData.get(level, position).getType().saturationMultiplier());
    }
}
