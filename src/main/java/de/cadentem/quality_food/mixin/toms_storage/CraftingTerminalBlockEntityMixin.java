package de.cadentem.quality_food.mixin.toms_storage;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Apply quality when items are converted from / to their storage variants */
@Mixin(TileEntityCraftingTerminal.class)
public abstract class CraftingTerminalBlockEntityMixin {
    @Shadow(remap = false) @Final private CraftingContainer craftMatrix;
    @Shadow(remap = false) private CraftingRecipe currentRecipe;

    @ModifyArg(method = "onCraftingMatrixChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private ItemStack quality_food$handleConversion(final ItemStack stack) {
        QualityUtils.handleConversion(stack, craftMatrix, currentRecipe);
        return stack;
    }
}
