package de.cadentem.quality_food.mixin.fastbench;

import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import dev.shadowsoffire.fastbench.util.CraftResultSlotExt;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftResultSlotExt.class)
public abstract class CraftResultSlotExtMixin extends ResultSlot {
    public CraftResultSlotExtMixin(final Player player, final CraftingContainer craftSlots, final Container container, int slot, int x, int y) {
        super(player, craftSlots, container, slot, x, y);
    }

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onCraftedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;I)V", shift = At.Shift.AFTER))
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        ResultSlotAccess access = (ResultSlotAccess) this;

        if (access.quality_food$getPlayer().level().isClientSide() || container instanceof RecipeCraftingHolder holder && ServerConfig.isNoQualityRecipe(holder.getRecipeUsed())) {
            return;
        }

        QualityUtils.applyQuality(stack, access.quality_food$getPlayer(), Bonus.additive(QualityUtils.getQualityBonus(access.quality_food$getCraftSlots())));
    }
}