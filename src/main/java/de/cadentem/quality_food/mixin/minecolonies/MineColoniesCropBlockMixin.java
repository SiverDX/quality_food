package de.cadentem.quality_food.mixin.minecolonies;

import com.llamalad7.mixinextras.sugar.Local;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.core.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecoloniesCropBlock.class)
public abstract class MineColoniesCropBlockMixin {
    /** Unsure - apply quality in case the crop can grow into other directions */
    @Inject(method = "attemptGrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public void quality_food$applyQuality(final BlockState state, final ServerLevel level, final BlockPos position, final CallbackInfo callback, @Local(name = "offset") final BlockPos offset) {
        Quality quality = LevelData.get(level, position);

        if (quality.level() > 0) {
            LevelData.set(level, offset, quality);
        }
    }
}
