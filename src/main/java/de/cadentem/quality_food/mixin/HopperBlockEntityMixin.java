package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

/** The hopper puts the item back if it cannot be inserted - the consumed cooking queue entry needs to be restored in that case */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @WrapMethod(method = "tryTakeInItemFromSlot(Lnet/minecraft/world/level/block/entity/Hopper;Lnet/minecraft/world/Container;ILnet/minecraft/core/Direction;)Z")
    private static boolean quality_food$restoreCookingQueue(final Hopper hopper, final Container container, int slot, final Direction direction, final Operation<Boolean> original) {
        if (slot != 2 || !(container instanceof AbstractFurnaceBlockEntity furnace) || container.getItem(slot).isEmpty()) {
            return original.call(hopper, container, slot, direction);
        }

        BlockData data = furnace.getData(AttachmentHandler.BLOCK_DATA);
        BlockData.Snapshot snapshot = data.createSnapshot();
        boolean result = original.call(hopper, container, slot, direction);

        if (!result) {
            data.restoreSnapshot(snapshot);
        }

        return result;
    }
}
