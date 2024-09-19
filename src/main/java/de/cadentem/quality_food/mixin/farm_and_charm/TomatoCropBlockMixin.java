package de.cadentem.quality_food.mixin.farm_and_charm;

import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.block.crops.TomatoCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Have more context for dropped loot */
@Mixin(TomatoCropBlock.class)
public abstract class TomatoCropBlockMixin {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/satisfy/farm_and_charm/block/crops/TomatoCropBlock;dropTomatoes(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", shift = At.Shift.BEFORE, remap = false))
    private void quality_food$setContext(final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult result, final CallbackInfoReturnable<InteractionResult> callback) {
        DropData.current.set(DropData.create(LevelData.get(level, position), state, player, level.getBlockState(position.below())));
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/satisfy/farm_and_charm/block/crops/TomatoCropBlock;dropTomatoes(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", shift = At.Shift.AFTER, remap = false))
    private void quality_food$clearContext(final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult result, final CallbackInfoReturnable<InteractionResult> callback) {
        DropData.current.remove();
    }
}
