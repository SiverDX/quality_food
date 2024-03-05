package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;

/** Support for quality block state */
@Mixin(TomatoVineBlock.class)
public abstract class TomatoVineBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityForTick(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }

    @ModifyArg(method = "attemptRopeClimb", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState quality_food$keepQualityForTick(final BlockState original, @Local(argsOnly = true) final ServerLevel serverLevel, @Local(argsOnly = true) final BlockPos position) {
        BlockState state = serverLevel.getBlockState(position);

        if (state.is(original.getBlock())) {
            return original.setValue(Utils.QUALITY_STATE, state.getValue(Utils.QUALITY_STATE));
        }

        return original;
    }
}
