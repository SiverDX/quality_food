package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Allow material (with quality) to grant its bonus when manually taking out the result item */
@Mixin(FurnaceResultSlot.class)
public abstract class FurnaceResultSlotMixin extends Slot {
    @Shadow @Final private Player player;

    public FurnaceResultSlotMixin(final Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "checkTakeAchievements", at = @At(value = "RETURN"))
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        if (player.level().isClientSide()) {
            return;
        }

        if (container instanceof BlockEntity blockEntity && !QualityUtils.hasQuality(stack)) {
            Utils.useQuality(blockEntity, stack, player);
        }
    }
}
