package de.cadentem.quality_food.mixin.collectorsreap;

import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.core.Quality;
import net.brdle.collectorsreap.common.block.TallBushCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TallBushCropBlock.class)
public abstract class TallBushCropBlockMixin {
    @Inject(method = "grow", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1, shift = At.Shift.AFTER))
    private void quality_food$applyQuality(final ServerLevel level, final BlockState state, final BlockPos position, final int increment, final CallbackInfo callback) {
        Quality quality = LevelData.get(level, position);

        if (quality.level() > 0) {
            // TODO :: retrieve the boolean to check if 'setBlock' was successful before applying quality?
            LevelData.set(level, position.above(), quality);
        }
    }
}
