package de.cadentem.quality_food.mixin.sophisticatedcore;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingUpgradeContainer.class, remap = false)
public interface CraftingUpgradeContainerAccess {
    @Accessor("lastRecipe")
    @Nullable CraftingRecipe quality_food$getLastRecipe();
}
