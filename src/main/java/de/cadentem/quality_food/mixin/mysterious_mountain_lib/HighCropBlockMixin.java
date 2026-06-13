package de.cadentem.quality_food.mixin.mysterious_mountain_lib;

import cn.mcmod_mmf.mmlib.block.HighCropBlock;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Handle two-block tall crop growth quality retention
@Mixin(HighCropBlock.class)
public abstract class HighCropBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState quality_food$retainQualityOnTick(final BlockState grown, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) BlockPos position) {
        Utils.storeQuality(grown, level, position, Direction.UP);
        return grown;
    }

    @ModifyArg(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$retainQualityOnBonemeal(final BlockState grown, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) BlockPos position) {
        Utils.storeQuality(grown, level, position, Direction.UP);
        return grown;
    }
}
