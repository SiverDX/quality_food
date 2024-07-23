package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Apply quality bonus when eating */
@Mixin(CakeBlock.class)
public class CakeBlockMixin {
    @ModifyArg(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static int quality_food$modifyNutrition(int nutrition, @Local(argsOnly = true) final BlockState state) {
//        return (int) (nutrition * QualityConfig.getNutritionMultiplier(Quality.get(state.getValue(Utils.QUALITY_STATE))));
        return nutrition;
    }

    @ModifyArg(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static float quality_food$modifySaturation(float saturation, @Local(argsOnly = true) final BlockState state) {
//        return saturation * QualityConfig.getSaturationMultiplier(Quality.get(state.getValue(Utils.QUALITY_STATE)));
        return saturation;
    }
}
