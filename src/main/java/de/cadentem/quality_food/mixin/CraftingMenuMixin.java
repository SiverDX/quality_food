package de.cadentem.quality_food.mixin;

import net.minecraft.server.level.ServerLevel;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends AbstractCraftingMenu {

    public CraftingMenuMixin(final MenuType<?> type, int containerId, int width, int height) {
        super(type, containerId, width, height);
    }

    /** Apply quality when crafting with shift-click */
    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/CraftingMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        if (ServerConfig.isNoQualityRecipe(resultSlots.getRecipeUsed(), player.registryAccess())) {
            return;
        }

        QualityUtils.applyQuality(stack, craftSlots.getItems(), player, player.registryAccess());
    }

    /** Apply quality when items are converted from / to their storage variants */
    @ModifyVariable(method = "slotChangedCraftingGrid", at = @At(value = "STORE"), name = "result")
    private static ItemStack quality_food$handleConversion(final ItemStack result, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) final CraftingContainer container, @Local(argsOnly = true) final ResultContainer resultContainer) {
        QualityUtils.handleConversion(result, container, resultContainer.getRecipeUsed(), level.registryAccess());
        return result;
    }
}
