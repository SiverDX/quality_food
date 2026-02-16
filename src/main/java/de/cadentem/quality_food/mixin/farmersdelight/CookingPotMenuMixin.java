package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

/** Allow material (with quality) to grant its bonus when shift-clicking the crafting result */
@Mixin(CookingPotMenu.class)
public abstract class CookingPotMenuMixin {
    @Shadow(remap = false) @Final public CookingPotBlockEntity blockEntity;

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/entity/container/CookingPotMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(name = "slotStack") final ItemStack stack) {
        if (player.level().isClientSide()) {
            return;
        }

        Utils.useQuality(blockEntity, stack, player);
    }
}
