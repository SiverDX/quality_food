package de.cadentem.quality_food.mixin.fastbench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResultSlot.class)
public interface ResultSlotAccess {
    @Accessor("player")
    Player quality_food$getPlayer();

    @Accessor("craftSlots")
    CraftingContainer quality_food$getCraftSlots();
}
