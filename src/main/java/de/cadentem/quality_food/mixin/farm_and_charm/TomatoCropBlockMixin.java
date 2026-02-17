package de.cadentem.quality_food.mixin.farm_and_charm;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.crops.TomatoCropBodyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Have more context for dropped loot */
@Mixin(TomatoCropBodyBlock.class)
public abstract class TomatoCropBlockMixin {
    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private void qualtiy_food$storeQuality(final ServerLevel level, final RandomSource random, final BlockPos position, final BlockState state, final CallbackInfo callback) {
        Utils.storeQuality(state, level, position, position.above());
    }
}
