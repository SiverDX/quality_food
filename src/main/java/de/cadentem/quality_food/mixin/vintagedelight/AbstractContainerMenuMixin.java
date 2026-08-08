package de.cadentem.quality_food.mixin.vintagedelight;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.ribs.vintagedelight.screen.FermentingJarMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO :: Might need to be generic in the future
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Inject(method = "lambda$doClick$3", at = @At("HEAD"))
    private void injectQualityFood$doClick(final Slot slot, final Player player, final ItemStack stack, final CallbackInfo callback) {
        if (player.level().isClientSide() || slot.getSlotIndex() != 7) {
            return;
        }

        if ((Object) this instanceof FermentingJarMenu menu) {
            Utils.useQuality(menu.blockEntity, stack, player);
        }
    }
}
