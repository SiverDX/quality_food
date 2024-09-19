package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.RiceBlock;

/** Set quality for the newly grown block (i.e. above the base block) */
@Mixin(RiceBlock.class)
public abstract class RiceBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState quality_food$keepQualityWhenGrowingUpward(final BlockState grown, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) final BlockPos position) {
        Utils.storeQuality(grown, level, position, Direction.UP);
        return grown;
    }

    @ModifyArg(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityOnBonemeal(final BlockState grown, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) final BlockPos position) {
        Utils.storeQuality(grown, level, position, Direction.UP);
        return grown;
    }
}
