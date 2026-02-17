package de.cadentem.quality_food.mixin.herbalbrews;

import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.herbalbrews.core.blocks.TeaCupBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Don't apply to stored items quality when breaking this block */
@Mixin(TeaCupBlock.class)
public abstract class TeaCupBlockMixin {
    @Inject(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/satisfy/herbalbrews/core/blocks/TeaCupBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private void quality_food$skipApplyQuality(final Level level, final Player player, final BlockPos position, final BlockState state, final BlockEntity blockEntity, final ItemStack tool, final CallbackInfo callback) {
        DropData.CURRENT.set(DropData.SKIP);
    }

    @Inject(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/satisfy/herbalbrews/core/blocks/TeaCupBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void quality_food$resetDropData(final Level level, final Player player, final BlockPos position, final BlockState state, final BlockEntity blockEntity, final ItemStack tool, final CallbackInfo callback) {
        DropData.CURRENT.remove();
    }
}
