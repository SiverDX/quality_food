package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Support for quality block state */
@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityForTick(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }

    @ModifyArg(method = "growCrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private BlockState quality_food$keepQualityWhenGrowing(final BlockState original, @Local(argsOnly = true) final BlockState instance) {
        return original.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
    }
}
