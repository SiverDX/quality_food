package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Block.class)
public class BlockMixin {
    @Unique private static final ThreadLocal<DropData> quality_food$storedData = new ThreadLocal<>();

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final LootContext.Builder context, final CallbackInfo callback) {
        quality_food$storedData.set(DropData.create(state, null));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final LootContext.Builder context, final CallbackInfo callback) {
        quality_food$storedData.remove();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final Level level, final BlockPos position, final CallbackInfo callback) {
        quality_food$storedData.set(DropData.create(state, null));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final Level level, final BlockPos position, final CallbackInfo callback) {
        quality_food$storedData.remove();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final LevelAccessor level, final BlockPos position, final @Nullable BlockEntity blockEntity, final CallbackInfo callback) {
        quality_food$storedData.set(DropData.create(state, null));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final LevelAccessor level, final BlockPos position, final @Nullable BlockEntity blockEntity, final CallbackInfo callback) {
        quality_food$storedData.remove();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final Level level, final BlockPos position, final @Nullable BlockEntity blockEntity, final Entity entity, final ItemStack tool, final CallbackInfo callback) {
        quality_food$storedData.set(DropData.create(state, entity));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final Level level, final BlockPos position, final @Nullable BlockEntity blockEntity, final Entity entity, final ItemStack tool, final CallbackInfo callback) {
        quality_food$storedData.remove();
    }

    // Used for Cave Vines (i.e. Glow Berries)
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private static void quality_food$applyQuality(final Level level, final BlockPos position, final ItemStack stack, final CallbackInfo callback) {
        if (quality_food$storedData.get() == null) {
            QualityUtils.applyQuality(stack, level.getBlockState(position), null);
        }
    }

    /** Apply quality to block drops (not a loot modifier to make it also work with right-click harvesting) */
    @ModifyVariable(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack quality_food$applyQuality(final ItemStack stack) {
        DropData dropData = quality_food$storedData.get();

        if (dropData == null) {
            QualityUtils.applyQuality(stack);
            return stack;
        }

        QualityUtils.applyQuality(stack, dropData.state(), dropData.player());
        return stack;
    }
}
