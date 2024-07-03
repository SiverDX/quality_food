package de.cadentem.quality_food.mixin.majruszsenchantments;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.majruszsenchantments.enchantments.TelekinesisEnchantment;
import com.mlib.contexts.OnLoot;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TelekinesisEnchantment.Handler.class, remap = false)
public abstract class TelekinesisEnchantmentMixin {
    // TODO :: Check first if quality would apply and if it does then apply the quality, otherwise drop the item early? or do nothing (if the inventory is full)?
    @Inject(method = "addToInventory", at = @At(value = "HEAD"))
    private void applyQuality(final OnLoot.Data data, final Entity entity, final CallbackInfo callback, @Share("removed") final LocalRef<Boolean> wasRemoved) {
        if (!(entity instanceof Player player)) {
            return;
        }

        data.generatedLoot.removeIf(stack -> {
            if (player.getInventory().getFreeSlot() == -1 && player.getInventory().getSlotWithRemainingSpace(stack) == -1) {
                // To avoid trying to apply quality multiple times for the same stack
                return false;
            }

            if (data.blockState != null) {
                QualityUtils.applyQuality(stack, data.blockState, player, data.origin != null ? player.getLevel().getBlockState(new BlockPos(data.origin).below()) : null);
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

    @ModifyExpressionValue(method = "addToInventory", at = @At(value = "INVOKE", target = "Ljava/util/List;removeIf(Ljava/util/function/Predicate;)Z"))
    private boolean shouldTriggerParticles(boolean original, @Share("removed") final LocalRef<Boolean> wasRemoved) {
        return original || (wasRemoved.get() != null && wasRemoved.get());
    }
}
