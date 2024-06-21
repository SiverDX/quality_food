package de.cadentem.quality_food.mixin.farmersdelight;

import com.google.common.util.concurrent.AtomicDouble;
import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotResultSlot;

/** Allow material (with quality) to grant its bonus when manually taking out the result item */
@Mixin(CookingPotResultSlot.class)
public abstract class CookingPotResultSlotMixin extends SlotItemHandler {
    public CookingPotResultSlotMixin(final IItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
    }

    @Inject(method = "checkTakeAchievements", at = @At(value = "RETURN"))
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        if (player.level().isClientSide()) {
            return;
        }

        AtomicDouble bonus = new AtomicDouble(0);
        BlockDataProvider.getCapability(tileEntity).ifPresent(data -> bonus.set(data.useQuality()));
        QualityUtils.applyQuality(stack, player, Bonus.additive(bonus.floatValue()));
    }

    @Shadow(remap = false) @Final private Player player;
    @Shadow(remap = false) @Final public CookingPotBlockEntity tileEntity;
}
