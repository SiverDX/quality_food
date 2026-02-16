package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SidedInvWrapper.class, remap = false)
public abstract class SidedInvWrapperMixin {
    @Shadow @Final protected WorldlyContainer inv;

    @Inject(method = "extractItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldlyContainer;setChanged()V"))
    private void quality_food$applyQuality(final CallbackInfoReturnable<ItemStack> callback, @Local(name = "ret") final ItemStack stack) {
        if (inv instanceof AbstractFurnaceBlockEntity furnace) {
            //noinspection DataFlowIssue -> previous check prevents null
            if (furnace.hasLevel() && furnace.getLevel().isClientSide()) {
                return;
            }

            Utils.useQuality(furnace, stack, null);
        }
    }
}
