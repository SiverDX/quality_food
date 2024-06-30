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
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final LootContext.Builder context, final CallbackInfo callback) {
        DropData.current.set(DropData.create(state, null, context.getLevel().getBlockState(new BlockPos(context.getParameter(LootContextParams.ORIGIN)).below())));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final LootContext.Builder context, final CallbackInfo callback) {
        DropData.current.remove();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final Level level, final BlockPos position, final CallbackInfo callback) {
        DropData.current.set(DropData.create(state, null, level.getBlockState(position.below())));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final Level level, final BlockPos position, final CallbackInfo callback) {
        DropData.current.remove();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final LevelAccessor level, final BlockPos position, final @Nullable BlockEntity blockEntity, final CallbackInfo callback) {
        DropData.current.set(DropData.create(state, null, level.getBlockState(position.below())));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final LevelAccessor level, final BlockPos position, final @Nullable BlockEntity blockEntity, final CallbackInfo callback) {
        DropData.current.remove();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final Level level, final BlockPos position, final @Nullable BlockEntity blockEntity, final Entity entity, final ItemStack tool, final CallbackInfo callback) {
        DropData.current.set(DropData.create(state, entity, level.getBlockState(position.below())));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private static void quality_food$clearBlockState(final BlockState state, final Level level, final BlockPos position, final @Nullable BlockEntity blockEntity, final Entity entity, final ItemStack tool, final CallbackInfo callback) {
        DropData.current.remove();
    }

    // Used for Cave Vines (i.e. Glow Berries)
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private static void quality_food$applyQuality(final Level level, final BlockPos position, final ItemStack stack, final CallbackInfo callback) {
        if (DropData.current.get() == null) {
            QualityUtils.applyQuality(stack, level.getBlockState(position), null, level.getBlockState(position.below()));
        }
    }

    /** Apply quality to block drops (not a loot modifier to make it also work with right-click harvesting) */
    @ModifyVariable(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack quality_food$applyQuality(final ItemStack stack) {
        DropData dropData = DropData.current.get();

        if (dropData == null) {
            QualityUtils.applyQuality(stack);
        } else {
            QualityUtils.applyQuality(stack, dropData.state(), dropData.player(), dropData.farmland());
        }

        return stack;
    }
}
