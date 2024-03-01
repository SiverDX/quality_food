package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.block.FeastBlock;

/** Add support for the quality block state (and apply quality to served item) */
@Mixin(FeastBlock.class)
public class FeastBlockMixin {
    @ModifyReturnValue(method = "getServingItem", at = @At("RETURN"), remap = false)
    private ItemStack quality_food$applyQualityToItem(final ItemStack stack, /* Method parameters: */ final BlockState state) {
        QualityUtils.applyQuality(stack, Quality.getRaw(state.getValue(Utils.QUALITY_STATE)));
        return stack;
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState quality_food$setQualityState(final BlockState original, /* Method parameters: */ final BlockPlaceContext context) {
        return original.setValue(Utils.QUALITY_STATE, QualityUtils.getPlacementQuality(context.getItemInHand()));
    }
}
