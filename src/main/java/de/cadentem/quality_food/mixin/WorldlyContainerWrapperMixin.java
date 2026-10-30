package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = WorldlyContainerWrapper.class, remap = false)
public abstract class WorldlyContainerWrapperMixin {
    @Shadow @Final private WorldlyContainer container;
    @Shadow @Final private VanillaContainerWrapper wrapper;
    @Shadow protected abstract int convertSlot(int slot);

    @WrapMethod(method = "getResource(I)Lnet/neoforged/neoforge/transfer/item/ItemResource;")
    private ItemResource quality_food$getResource(int index, final Operation<ItemResource> original) {
        ItemResource resource = original.call(index);

        if (!resource.isEmpty() && container instanceof AbstractFurnaceBlockEntity furnace && convertSlot(index) == 2 && furnace.hasLevel() && !furnace.getLevel().isClientSide()) {
            BlockData data = furnace.getData(AttachmentHandler.BLOCK_DATA);
            ItemStack preview = data.previewQuality(resource.toStack(), furnace.getLevel());
            return ItemResource.of(preview);
        }

        return resource;
    }

    @WrapMethod(method = "extract(ILnet/neoforged/neoforge/transfer/item/ItemResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I")
    private int quality_food$extract(int index, ItemResource resource, int amount, final TransactionContext transaction, final Operation<Integer> original) {
        if (!resource.isEmpty() && container instanceof AbstractFurnaceBlockEntity furnace && convertSlot(index) == 2 && furnace.hasLevel() && !furnace.getLevel().isClientSide()) {
            ItemResource currentResource = ((WorldlyContainerWrapper) (Object) this).getResource(index);

            if (resource.equals(currentResource)) {
                resource = wrapper.getResource(2);
            }

            // Known limitation: a caller-built resource without quality (e.g. an item filter template) still matches the slot
            // The items are extracted without quality while the cooking queue is consumed
            // Rejecting it instead would block such extractions completely

            // The previewed quality only applies to the head entries which are identical
            int headRunLength = furnace.getData(AttachmentHandler.BLOCK_DATA).getHeadRunLength();

            if (headRunLength > 0) {
                amount = Math.min(amount, headRunLength);
            }
        }

        return original.call(index, resource, amount, transaction);
    }
}

