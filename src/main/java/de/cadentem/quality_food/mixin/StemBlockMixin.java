package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Set quality for the newly grown block (i.e. above the base block) */
@Mixin(StemBlock.class)
public abstract class StemBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private BlockState quality_food$keepQualityForGrow(final BlockState grown, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) final BlockPos position, @Local(ordinal = 1) final BlockPos relativePosition) {
        if (Utils.isValidBlock(grown.getBlock())) {
            LevelData data = level.getData(AttachmentHandler.LEVEL_DATA);
            Quality quality = data.get(position);

            if (quality != Quality.NONE) {
                data.set(relativePosition, quality);
            }
        }

        return grown;
    }
}
