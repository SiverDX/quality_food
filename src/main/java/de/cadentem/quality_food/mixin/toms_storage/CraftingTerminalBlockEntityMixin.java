package de.cadentem.quality_food.mixin.toms_storage;

import com.tom.storagemod.tile.CraftingTerminalBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CraftingTerminalBlockEntity.class)
public abstract class CraftingTerminalBlockEntityMixin {
    @ModifyArg(method = "onCraftingMatrixChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack handleConversion(final ItemStack stack) {
        QualityUtils.handleConversion(stack, craftMatrix);
        return stack;
    }

    @Shadow(remap = false) @Final private CraftingContainer craftMatrix;
}
