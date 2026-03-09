package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.satisfy.herbalbrews.client.gui.handler.TeaKettleGuiHandler;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(TeaKettleGuiHandler.class)
public abstract class TeaKettleGuiHandlerMixin {
    @Shadow(remap = false) @Final private ContainerData propertyDelegate;

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/satisfy/herbalbrews/client/gui/handler/TeaKettleGuiHandler;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(name = "item") final ItemStack stack) {
        if (player.level().isClientSide()) {
            return;
        }

        try {
            // Block entity is hidden inside an anonymous inner class
            Field field = propertyDelegate.getClass().getDeclaredField("this$0");
            field.setAccessible(true);

            Utils.useQuality((TeaKettleBlockEntity) field.get(propertyDelegate), stack, player);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            QualityFood.LOG.error("Failed to apply quality", exception);
        }
    }
}
