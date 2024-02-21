package de.cadentem.quality_food.mixin.farmersdelight;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

/** Allow material (with quality) to grant its bonus when shift-clicking the crafting result */
@Mixin(CookingPotMenu.class)
public abstract class CookingPotMenuMixin extends RecipeBookMenu<Container> {
    public CookingPotMenuMixin(final MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/entity/container/CookingPotMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        if (player.getLevel().isClientSide()) {
            return;
        }

        AtomicDouble bonus = new AtomicDouble(0);
        BlockDataProvider.getCapability(blockEntity).ifPresent(data -> bonus.set(data.useQuality()));
        QualityUtils.applyQuality(stack, player, bonus.floatValue());
    }

    @Shadow(remap = false) @Final public CookingPotBlockEntity blockEntity;
}
