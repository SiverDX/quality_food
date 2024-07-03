package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Not using crafting event because recipe access is needed */
@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin extends Slot {
    @Shadow @Final private Player player;
    @Shadow @Final private CraftingContainer craftSlots;

    public ResultSlotMixin(final Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onCraftedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;I)V", shift = At.Shift.AFTER))
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        if (player.getLevel().isClientSide() || container instanceof RecipeHolder holder && ServerConfig.isNoQualityRecipe(holder.getRecipeUsed())) {
            return;
        }

        QualityUtils.applyQuality(stack, player, Bonus.additive(QualityUtils.getQualityBonus(craftSlots)));
    }
}
