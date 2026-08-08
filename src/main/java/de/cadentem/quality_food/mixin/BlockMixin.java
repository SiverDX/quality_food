package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.core.loot_modifiers.QualityLootModifier;
import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
    /** Set missing drop data context if needed */
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private static void quality_food$setDropData(final Level level, final BlockPos position, final ItemStack stack, final CallbackInfo callback, @Share("flag") final LocalRef<Boolean> flagRef) {
        if (DropData.CURRENT.get() == null) {
            DropData.CURRENT.set(new DropData(LevelData.get(level, position, true), position, level.getBlockState(position), null, level.getBlockState(position.below())));
            flagRef.set(true);
        }
    }

    /** Clear the previously set drop data context if needed */
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private static void quality_food$clearDropData(final Level level, final BlockPos position, final ItemStack stack, final CallbackInfo callback, @Share("flag") final LocalRef<Boolean> flagRef) {
        Boolean flag = flagRef.get();

        if (flag != null) {
            // Only clear context if it was set from this method
            DropData.CURRENT.remove();
        }
    }

    /** Set missing drop data context if needed */
    @Inject(method = "popResourceFromFace", at = @At("HEAD"))
    private static void quality_food$setDropData(final Level level, final BlockPos position, final Direction direction, final ItemStack stack, final CallbackInfo callback, @Share("flag") final LocalRef<Boolean> flagRef) {
        if (DropData.CURRENT.get() == null) {
            DropData.CURRENT.set(new DropData(LevelData.get(level, position, true), position, level.getBlockState(position), null, level.getBlockState(position.below())));
            flagRef.set(true);
        }
    }

    /** Clear the previously set drop data context if needed */
    @Inject(method = "popResourceFromFace", at = @At("TAIL"))
    private static void quality_food$clearDropData(final Level level, final BlockPos position, final Direction direction, final ItemStack stack, final CallbackInfo callback, @Share("flag") final LocalRef<Boolean> flagRef) {
        Boolean flag = flagRef.get();

        if (flag != null) {
            // Only clear context if it was set from this method
            DropData.CURRENT.remove();
        }
    }

    /** Apply quality to block drops (not a loot modifier to make it also work with right-click harvesting) */
    @ModifyVariable(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack quality_food$applyQuality(final ItemStack stack) {
        DropData dropData = DropData.CURRENT.get();

        if (dropData == DropData.SKIP || dropData != null && dropData.position().equals(QualityLootModifier.lastProcessedPosition)) {
            return stack;
        }

        if (dropData == null) {
            QualityUtils.applyQuality(stack, (Player) null);
        } else {
            QualityUtils.applyQuality(stack, dropData.state(), dropData.quality(), dropData.player(), dropData.farmland());
        }

        return stack;
    }
}
