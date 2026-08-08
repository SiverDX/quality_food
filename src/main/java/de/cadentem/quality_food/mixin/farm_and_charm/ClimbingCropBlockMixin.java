package de.cadentem.quality_food.mixin.farm_and_charm;

import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClimbingCropBlock.class)
public abstract class ClimbingCropBlockMixin {
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/satisfy/farm_and_charm/core/block/crops/ClimbingCropBlock;dropFruits(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", shift = At.Shift.BEFORE))
    private void quality_food$setContext(final BlockState state, final Level level, final BlockPos position, final Player player, final BlockHitResult hit, final CallbackInfoReturnable<InteractionResult> callback) {
        DropData.CURRENT.set(DropData.create(LevelData.get(level, position), position, state, player, level.getBlockState(position.below())));
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/satisfy/farm_and_charm/core/block/crops/ClimbingCropBlock;dropFruits(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", shift = At.Shift.AFTER))
    private void quality_food$clearContext(final BlockState state, final Level level, final BlockPos position, final Player player, final BlockHitResult hit, final CallbackInfoReturnable<InteractionResult> callback) {
        DropData.CURRENT.remove();
    }
}
