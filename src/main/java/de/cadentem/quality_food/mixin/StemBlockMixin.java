package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Set quality for the newly grown block (i.e. above the base block) */
@Mixin(StemBlock.class)
public abstract class StemBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private BlockState quality_food$keepQualityForGrow(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }
}
