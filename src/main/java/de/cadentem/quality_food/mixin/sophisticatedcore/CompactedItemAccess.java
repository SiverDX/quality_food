package de.cadentem.quality_food.mixin.sophisticatedcore;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.util.RecipeHelper$CompactedItem", remap = false)
public interface CompactedItemAccess {
    @Dynamic
    @Accessor("item")
    ItemStack quality_food$getItem();
}
