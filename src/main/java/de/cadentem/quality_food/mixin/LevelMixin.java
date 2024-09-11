package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.capability.LevelDataProvider;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Retain quality when the block changes (e.g. crop age) */
@Mixin(Level.class)
public abstract class LevelMixin {
    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", shift = At.Shift.AFTER), argsOnly = true)
    private BlockState quality_food$retainQuality(final BlockState newState, @Local(argsOnly = true) final BlockPos position, @Local(ordinal = 1) final BlockState oldState) {
        Level level = (Level) (Object) this;

        if (level.isClientSide()) {
            return newState;
        }

        if (!Utils.isValidBlock(newState.getBlock()) && Utils.isValidBlock(oldState.getBlock())) {
            LevelData data = LevelDataProvider.getOrNull(level);

            if (data != null) {
                data.remove(position);
            }
        }

        return newState;
    }
}
