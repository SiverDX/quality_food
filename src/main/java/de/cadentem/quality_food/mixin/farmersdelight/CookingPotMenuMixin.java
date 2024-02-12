package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.RarityUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

@Mixin(CookingPotMenu.class)
public abstract class CookingPotMenuMixin extends RecipeBookMenu<Container> {
    public CookingPotMenuMixin(final MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/entity/container/CookingPotMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void applyRarity(final Player player, int slot, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        if (player.getLevel().isClientSide()) {
            return;
        }

        RarityUtils.applyRarity(stack, player.getRandom(), player.getLuck() + RarityUtils.getRarityBonus(slots, getResultSlotIndex()));
    }
}
