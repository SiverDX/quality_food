package de.cadentem.quality_food.mixin.farm_and_charm;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.block.crops.TomatoCropHeadBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Set quality for the newly grown block (i.e. above the base block) */
@Mixin(TomatoCropHeadBlock.class)
public abstract class TomatoCropHeadBlockMixin {
    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void qualtiy_food$storeQuality(final BlockState state, final ServerLevel level, final BlockPos position, final RandomSource random, final CallbackInfo callback) {
        Utils.storeQuality(state, level, position, position.above());
    }

    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private void qualtiy_food$storeQuality(final ServerLevel level, final RandomSource random, final BlockPos position, final BlockState state, final CallbackInfo callback) {
        Utils.storeQuality(state, level, position, position.above());
    }
}
