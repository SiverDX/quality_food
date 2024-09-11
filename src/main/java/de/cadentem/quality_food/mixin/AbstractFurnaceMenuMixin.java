package de.cadentem.quality_food.mixin;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Allow material (with quality) to grant its bonus when shift-clicking the crafting result */
@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin extends RecipeBookMenu<Container> {
    @Shadow @Final private Container container;

    public AbstractFurnaceMenuMixin(final MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractFurnaceMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        AtomicDouble bonus = new AtomicDouble(0);

        if (container instanceof AbstractFurnaceBlockEntity furnace && !player.level().isClientSide()) {
            BlockDataProvider.getCapability(furnace).ifPresent(data -> bonus.set(data.useQuality()));
        }

        QualityUtils.applyQuality(stack, player, Bonus.additive(bonus.floatValue()));
    }
}
