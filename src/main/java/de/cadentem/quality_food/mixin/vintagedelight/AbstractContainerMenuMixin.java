package de.cadentem.quality_food.mixin.vintagedelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.ribs.vintagedelight.screen.FermentingJarMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

// TODO :: Might need to be generic in the future
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyVariable(method = "doClick", at = @At(value = "STORE", ordinal = 0))
    private Optional<ItemStack> injectQualityFood$doClick(final Optional<ItemStack> stack, @Local(argsOnly = true) final Player player, @Local final Slot slot) {
        if (player.level().isClientSide() || slot.getSlotIndex() != 7) {
            return stack;
        }

        if ((Object) this instanceof FermentingJarMenu menu) {
            return stack.map(item -> {
                Utils.useQuality(menu.blockEntity, item, player);
                return item;
            });
        }

        return stack;
    }
}
