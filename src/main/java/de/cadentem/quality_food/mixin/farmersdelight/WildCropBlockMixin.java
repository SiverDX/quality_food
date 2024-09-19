package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.WildCropBlock;

@Mixin(WildCropBlock.class)
@Debug(export = true)
public abstract class WildCropBlockMixin {
    /** Position variable gets modified */
    @Inject(method = "performBonemeal", at = @At(value = "HEAD"))
    private void quality_food$storePosition(final ServerLevel level, final RandomSource random, final BlockPos position, final BlockState state, final CallbackInfo callback, @Share("originalPosition") final LocalRef<BlockPos> originalPosition) {
        originalPosition.set(position);
    }

    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void quality_food$storeQuality(final ServerLevel level, final RandomSource random, final BlockPos position, final BlockState state, final CallbackInfo callback, @Local(ordinal = 1) final BlockPos randomPosition, @Share("originalPosition") final LocalRef<BlockPos> originalPosition) {
        Utils.storeQuality(state, level, originalPosition.get(), randomPosition, 0.25);
    }
}
