package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Apply quality bonus when eating */
@Mixin(CakeBlock.class)
public class CakeBlockMixin {
    @ModifyArg(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static int quality_food$modifyNutrition(int nutrition, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (int) (nutrition * LevelData.get(level, position).getType().nutritionMultiplier());
    }

    @ModifyArg(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static float quality_food$modifySaturation(float saturation, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (float) (saturation * LevelData.get(level, position).getType().saturationMultiplier());
    }
}
