package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.RiceBlock;

/** Support for quality block state */
@Mixin(RiceBlock.class)
public abstract class RiceBlockMixin {
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState quality_food$keepQualityWhenGrowingUpward(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityWhenGrowing(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }

    @ModifyArg(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityOnBonemeal(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }
}
