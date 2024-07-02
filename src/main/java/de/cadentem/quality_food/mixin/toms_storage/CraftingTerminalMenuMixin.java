package de.cadentem.quality_food.mixin.toms_storage;

import com.llamalad7.mixinextras.sugar.Local;
import com.tom.storagemod.gui.CraftingTerminalMenu;
import com.tom.storagemod.gui.StorageTerminalMenu;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Inventory;
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

/** Apply quality when crafting with shift-click */
@Mixin(CraftingTerminalMenu.class)
public abstract class CraftingTerminalMenuMixin extends StorageTerminalMenu {
    @Shadow(remap = false) private @Final ResultContainer craftResult;
    @Shadow(remap = false) private @Final CraftingContainer craftMatrix;

    public CraftingTerminalMenuMixin(int id, final Inventory inventory) {
        super(id, inventory);
    }

    @Inject(method = "shiftClickItems", at = @At(value = "INVOKE", target = "Lcom/tom/storagemod/gui/CraftingTerminalMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int index, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        if (ServerConfig.isNoQualityRecipe(craftResult.getRecipeUsed())) {
            return;
        }

        QualityUtils.applyQuality(stack, player, Bonus.additive(QualityUtils.getQualityBonus(craftMatrix)));
    }
}
