package de.cadentem.quality_food.mixin.miners_delight;

import com.llamalad7.mixinextras.sugar.Local;
import com.sammy.minersdelight.content.block.copper_pot.CopperPotBlockEntity;
import com.sammy.minersdelight.content.block.copper_pot.CopperPotMenu;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Allow material (with quality) to grant its bonus when shift-clicking the crafting result */
@Mixin(CopperPotMenu.class)
public abstract class CopperPotMenuMixin {
    @Shadow(remap = false) @Final public CopperPotBlockEntity blockEntity;

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lcom/sammy/minersdelight/content/block/copper_pot/CopperPotMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        if (player.level().isClientSide()) {
            return;
        }

        Utils.useQuality(blockEntity, stack, player);
    }
}
