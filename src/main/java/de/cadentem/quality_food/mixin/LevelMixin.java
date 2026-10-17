package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "markAndNotifyBlock", at = @At("HEAD"))
    private void quality_food$removeQuality(final BlockPos position, final LevelChunk chunk, final BlockState oldState, final BlockState newState, int flags, int limit, final CallbackInfo callback) {
        Level level = (Level) (Object) this;
        if (!level.isClientSide() && !Utils.isValidBlock(newState.getBlock()) && Utils.isValidBlock(oldState.getBlock())) {
            level.getData(AttachmentHandler.LEVEL_DATA).remove(position);
        }
    }
}
