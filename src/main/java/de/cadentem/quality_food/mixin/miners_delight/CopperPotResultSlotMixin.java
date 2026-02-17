package de.cadentem.quality_food.mixin.miners_delight;

import com.sammy.minersdelight.content.block.copper_pot.CopperPotBlockEntity;
import com.sammy.minersdelight.content.block.copper_pot.CopperPotResultSlot;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Allow material (with quality) to grant its bonus when manually taking out the result item */
@Mixin(CopperPotResultSlot.class)
public abstract class CopperPotResultSlotMixin {
    @Shadow(remap = false) @Final private Player player;
    @Shadow(remap = false) @Final public CopperPotBlockEntity tileEntity;

    @Inject(method = "checkTakeAchievements", at = @At(value = "RETURN"))
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        if (player.level().isClientSide()) {
            return;
        }

        Utils.useQuality(tileEntity, stack, player);
    }
}
