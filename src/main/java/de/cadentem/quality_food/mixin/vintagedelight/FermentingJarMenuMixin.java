package de.cadentem.quality_food.mixin.vintagedelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.ribs.vintagedelight.block.entity.FermentingJarBlockEntity;
import net.ribs.vintagedelight.screen.FermentingJarMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FermentingJarMenu.class)
public abstract class FermentingJarMenuMixin {
    @Shadow @Final public FermentingJarBlockEntity blockEntity;

    /** Apply quality when crafting with shift-click */
    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/ribs/vintagedelight/screen/FermentingJarMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 2, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(name = "stack") final ItemStack stack) {
        if (!player.level().isClientSide()) {
            Utils.useQuality(blockEntity, stack, player);
        }
    }
}
