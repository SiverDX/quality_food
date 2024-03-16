package de.cadentem.quality_food.mixin.majruszsenchantments;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.majruszlibrary.events.OnLootGenerated;
import com.majruszsenchantments.enchantments.TelekinesisEnchantment;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TelekinesisEnchantment.class, remap = false)
public abstract class TelekinesisEnchantmentMixin {
    // TODO :: Check first if quality would apply and if it does then apply the quality, otherwise drop the item early? or do nothing (if the inventory is full)?
    @Inject(method = "addToInventory", at = @At(value = "HEAD"))
    private void applyQuality(final OnLootGenerated data, final Player player, final CallbackInfo callback, @Share("removed") final LocalRef<Boolean> wasRemoved) {
        data.generatedLoot.removeIf(stack -> {
            if (player.getInventory().getFreeSlot() == -1 && player.getInventory().getSlotWithRemainingSpace(stack) == -1) {
                // To avoid trying to apply quality multiple times for the same stack
                return false;
            }

            if (data.blockState != null) {
                QualityUtils.applyQuality(stack, data.blockState, player);
            } else {
                QualityUtils.applyQuality(stack, player);
            }

            boolean removed = player.addItem(stack);

            if (removed && wasRemoved.get() == null) {
                wasRemoved.set(true);
            }

            return removed;
        });
    }

    @ModifyExpressionValue(method = "addToInventory", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;removeIf(Ljava/util/function/Predicate;)Z"))
    private boolean shouldTriggerParticles(boolean original, @Share("removed") final LocalRef<Boolean> wasRemoved) {
        return original || (wasRemoved.get() != null && wasRemoved.get());
    }
}
