package de.cadentem.quality_food.mixin.quark;

import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.content.tweaks.module.SimpleHarvestModule;

// TODO :: shrink item stack (seed) with the proper quality and if not present place block with proper quality of shrunk item stack
@Mixin(value = SimpleHarvestModule.class, remap = false)
public abstract class SimpleHarvestModuleMixin {
    /** Roll quality with more context */
    @Inject(method = "harvestAndReplant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", shift = At.Shift.BEFORE, remap = true))
    private static void quality_food$setDropData(final Level level, final BlockPos position, final BlockState state, final LivingEntity livingEntity, final InteractionHand hand, final CallbackInfoReturnable<Boolean> callback) {
        DropData.current.set(new DropData(LevelData.get(level, position, true), state, livingEntity instanceof Player player ? player : null, level.getBlockState(position.below())));
    }

    /** Clear context */
    @Inject(method = "harvestAndReplant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V", shift = At.Shift.AFTER, remap = true))
    private static void quality_food$clearDropDAta(final Level level, final BlockPos position, final BlockState state, final LivingEntity livingEntity, final InteractionHand hand, final CallbackInfoReturnable<Boolean> callback) {
        DropData.current.remove();
    }
}
