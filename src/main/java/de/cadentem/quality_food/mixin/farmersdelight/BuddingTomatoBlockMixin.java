package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;

/** Support for quality block state */
@Debug(export = true)
@Mixin(BuddingTomatoBlock.class)
public abstract class BuddingTomatoBlockMixin {
    @ModifyArg(method = "growPastMaxAge", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState quality_food$keepQualityWhenGrowing(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }

    @ModifyArg(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityWhenUpdatingShape(final BlockState original, @Local(argsOnly = true, ordinal = 0) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }

    @ModifyArg(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
    private BlockState quality_food$keepQualityOnBonemeal(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }
}
