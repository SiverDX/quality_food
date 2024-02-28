package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Block.class)
public class BlockMixin {
    @Unique private static final ThreadLocal<DropData> quality_food$storedState = new ThreadLocal<>();

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final Level level, final BlockPos position, final CallbackInfo callback) {
        quality_food$storedState.set(DropData.create(state, null));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("HEAD"))
    private static void quality_food$storeBlockState(final BlockState state, final LevelAccessor level, final BlockPos position, final @Nullable BlockEntity blockEntity, final CallbackInfo callback) {
        quality_food$storedState.set(DropData.create(state, null));
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("HEAD"), remap = false)
    private static void quality_food$storeBlockState(final BlockState state, final Level level, final BlockPos position, final @Nullable BlockEntity blockEntity, final @Nullable Entity entity, final ItemStack tool, boolean dropExperience, final CallbackInfo callback) {
        quality_food$storedState.set(DropData.create(state, entity));
    }

    /** Apply quality to block drops (not a loot modifier, so it also works for right-click harvesting) */
    @ModifyVariable(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack quality_food$applyQuality(final ItemStack stack, /* Method arguments: */ final Level level) {
        DropData dropData = quality_food$storedState.get();
        Quality quality = dropData != null && dropData.state().hasProperty(Utils.QUALITY_STATE) ? Quality.get(dropData.state().getValue(Utils.QUALITY_STATE)) : Quality.NONE;
        float bonus = dropData != null ? dropData.bonus() : 0;

        if (quality != Quality.NONE) {
            QualityUtils.applyQuality(stack, quality);
        } else {
            QualityUtils.applyQuality(stack, level, bonus);
        }

        quality_food$storedState.remove();
        return stack;
    }

    /** Support for quality block state */
    @Inject(method = "createBlockStateDefinition", at = @At(value = "TAIL"))
    private void quality_food$addQualityProperty(final StateDefinition.Builder<Block, BlockState> builder, final CallbackInfo callback) {
        if (Utils.isValidBlock((Block) (Object) this)) {
            builder.add(Utils.QUALITY_STATE);
        }
    }

    /** Support for quality block state */
    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState quality_food$setQualityState(final BlockState original, /* Method parameters: */ BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();

        if (Utils.isValidBlock(original) && original.hasProperty(Utils.QUALITY_STATE)) {
            return original.setValue(Utils.QUALITY_STATE, QualityUtils.getQuality(itemInHand).ordinal());
        }

        return original;
    }
}
