package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.block.FeastBlock;

/** Apply quality to served item */
@Mixin(FeastBlock.class)
public class FeastBlockMixin {
    @ModifyReturnValue(method = "getServingItem", at = @At("RETURN"), remap = false)
    private ItemStack quality_food$applyQualityToItem(final ItemStack stack, /* Method parameters: */ final BlockState state) {
        QualityUtils.applyQuality(stack, Quality.get(state.getValue(Utils.QUALITY_STATE)));
        return stack;
    }
}
