package de.cadentem.quality_food.mixin.toms_storage;

import com.llamalad7.mixinextras.sugar.Local;
import com.tom.storagemod.gui.CraftingTerminalMenu;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingTerminalMenu.class)
@Debug(export = true)
public abstract class CraftingTerminalMenuMixin {
    @Inject(method = "shiftClickItems", at = @At(value = "INVOKE", target = "Lcom/tom/storagemod/gui/CraftingTerminalMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", shift = At.Shift.BEFORE))
    private void applyQuality(final Player player, int index, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        float bonus = 0;

        for (int slot = 0; slot < craftMatrix.getContainerSize(); slot++) {
            bonus += QualityUtils.getBonus(QualityUtils.getQuality(craftMatrix.getItem(slot)));
        }

        QualityUtils.applyQuality(stack, player, Bonus.additive(bonus));
    }

    @Shadow @Final private CraftingContainer craftMatrix;
}
