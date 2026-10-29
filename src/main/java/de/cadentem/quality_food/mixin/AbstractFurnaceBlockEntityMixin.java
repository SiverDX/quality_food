package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity {
    @Unique
    private final SnapshotJournal<BlockData.Snapshot> quality_food$cookingQueueJournal = new SnapshotJournal<>() {
        @Override
        protected BlockData.Snapshot createSnapshot() {
            return getData(AttachmentHandler.BLOCK_DATA).createSnapshot();
        }

        @Override
        protected void revertToSnapshot(final BlockData.Snapshot snapshot) {
            getData(AttachmentHandler.BLOCK_DATA).restoreSnapshot(snapshot);
        }
    };

    protected AbstractFurnaceBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void quality_food$handleParticles(final ServerLevel level, final BlockPos position, final BlockState state, final AbstractFurnaceBlockEntity furnace, final CallbackInfo callback) {
        Utils.sendParticles(level, furnace, position);
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void quality_food$incrementQuality(final ServerLevel level, final BlockPos position, final BlockState state, final AbstractFurnaceBlockEntity furnace, final CallbackInfo callback, @Local(name = "burnResult") final ItemStack result) {
        Utils.incrementQuality(furnace, List.of(furnace.getItem(0)), result);
    }

    @Inject(method = "onTransfer", at = @At("TAIL"), remap = false)
    private void quality_food$handleTransfer(int slot, int amountChange, final TransactionContext transaction, final CallbackInfo callback) {
        if (slot == 2 && amountChange < 0 && hasLevel() && !getLevel().isClientSide()) {
            quality_food$cookingQueueJournal.updateSnapshots(transaction);
            getData(AttachmentHandler.BLOCK_DATA).consumeQuality(-amountChange);
        }
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = super.removeItem(slot, count);

        if (slot == 2 && !result.isEmpty() && !Boolean.TRUE.equals(Utils.SKIP_ABSTRACT_FURNACE_REMOVE_QUALITY.get()) && hasLevel() && !getLevel().isClientSide()) {
            Utils.useQuality(this, result, null);
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = super.removeItemNoUpdate(slot);

        if (slot == 2 && !result.isEmpty() && hasLevel() && !getLevel().isClientSide()) {
            Utils.useQuality(this, result, null);
        }

        return result;
    }
}
