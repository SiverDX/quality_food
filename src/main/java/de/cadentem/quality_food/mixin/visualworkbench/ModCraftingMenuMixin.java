package de.cadentem.quality_food.mixin.visualworkbench;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModCraftingMenu.class)
public abstract class ModCraftingMenuMixin {
    @Shadow(remap = false) @Final private ResultContainer resultSlots;
    @Shadow(remap = false) @Final private CraftingContainer craftSlots;

    /** Apply quality when crafting with shift-click */
    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lfuzs/visualworkbench/world/inventory/ModCraftingMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        if (ServerConfig.isNoQualityRecipe(resultSlots.getRecipeUsed())) {
            return;
        }

        QualityUtils.applyQuality(stack, craftSlots.getItems(), player);
    }
}
