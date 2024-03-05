package de.cadentem.quality_food.mixin.sophisticatedcore;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CraftingUpgradeContainer.class)
public class CraftingUpgradeContainerMixin {
    @ModifyVariable(method = "updateCraftingResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultSlot;set(Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private ItemStack quality_food$handleConversion(final ItemStack stack, @Local(argsOnly = true) final CraftingContainer container) {
        QualityUtils.handleConversion(stack, container);
        return stack;
    }
}
