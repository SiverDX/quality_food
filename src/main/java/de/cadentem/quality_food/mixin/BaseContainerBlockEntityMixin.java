package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Apply quality when the furnace result is taken through the vanilla container interface (e.g. hoppers) */
@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerBlockEntityMixin {
    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void quality_food$applyFurnaceQuality(int slot, int count, final CallbackInfoReturnable<ItemStack> callback) {
        if (!Boolean.TRUE.equals(Utils.SKIP_ABSTRACT_FURNACE_REMOVE_QUALITY.get())) {
            quality_food$useFurnaceQuality(slot, callback.getReturnValue());
        }
    }

    @Inject(method = "removeItemNoUpdate(I)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void quality_food$applyFurnaceQualityNoUpdate(int slot, final CallbackInfoReturnable<ItemStack> callback) {
        quality_food$useFurnaceQuality(slot, callback.getReturnValue());
    }

    @Unique
    private void quality_food$useFurnaceQuality(int slot, final ItemStack result) {
        if (slot == 2 && !result.isEmpty() && (Object) this instanceof AbstractFurnaceBlockEntity furnace && furnace.hasLevel() && !furnace.getLevel().isClientSide()) {
            Utils.useQuality(furnace, result, null);
        }
    }
}
