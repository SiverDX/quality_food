package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Retain quality when the block changes (e.g. crop age) */
@Mixin(Level.class)
public abstract class LevelMixin {
    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", shift = At.Shift.AFTER), argsOnly = true)
    private BlockState quality_food$retainQuality(final BlockState newState, @Local(ordinal = 1) final BlockState oldState) {
        if (oldState.hasProperty(Utils.QUALITY_STATE) && newState.hasProperty(Utils.QUALITY_STATE)) {
            int ordinal = oldState.getValue(Utils.QUALITY_STATE);

            if (Quality.get(ordinal).level() > 0) {
                return newState.setValue(Utils.QUALITY_STATE, ordinal);
            }
        }

        return newState;
    }
}
