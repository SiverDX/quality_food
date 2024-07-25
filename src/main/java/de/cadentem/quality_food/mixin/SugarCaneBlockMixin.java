package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin {
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState quality_food$applyQuality(final BlockState grown, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) final BlockPos position) {
        if (Utils.isValidBlock(grown.getBlock())) {
            LevelData data = level.getData(AttachmentHandler.LEVEL_DATA);
            Quality quality = data.get(position);

            if (quality != Quality.NONE) {
                data.set(position.above(), quality);
            }
        }

        return grown;
    }
}
