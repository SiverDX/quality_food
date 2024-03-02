package de.cadentem.quality_food.mixin;

import com.google.common.util.concurrent.AtomicDouble;
import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Allow material (with quality) to grant its bonus when manually taking out the result item */
@Mixin(FurnaceResultSlot.class)
public abstract class FurnaceResultSlotMixin extends Slot {
    public FurnaceResultSlotMixin(final Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "checkTakeAchievements", at = @At(value = "RETURN"))
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        if (player.level().isClientSide()) {
            return;
        }

        if (container instanceof AbstractFurnaceBlockEntity blockEntity) {
            AtomicDouble bonus = new AtomicDouble(0);
            BlockDataProvider.getCapability(blockEntity).ifPresent(data -> bonus.set(data.useQuality()));
            QualityUtils.applyQuality(stack, player, Bonus.additive(bonus.floatValue()));
        }
    }

    @Shadow @Final private Player player;
}
