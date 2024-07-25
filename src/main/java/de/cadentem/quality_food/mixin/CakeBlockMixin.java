package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Apply quality bonus when eating */
@Mixin(CakeBlock.class)
public class CakeBlockMixin { // TODO 1.21 :: inject at head and use share?
    @ModifyArg(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static int quality_food$modifyNutrition(int nutrition, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        if (level instanceof ServerLevel serverLevel) {
            Quality quality = serverLevel.getData(AttachmentHandler.LEVEL_DATA).get(position);
            return (int) (nutrition * quality.getType().nutritionMultiplier());
        }

        return nutrition;
    }

    @ModifyArg(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static float quality_food$modifySaturation(float saturation, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        if (level instanceof ServerLevel serverLevel) {
            Quality quality = serverLevel.getData(AttachmentHandler.LEVEL_DATA).get(position);
            return (int) (saturation * quality.getType().saturationMultiplier());
        }

        return saturation;
    }
}
