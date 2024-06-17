package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin {
    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState quality_food$applyQualityState(final BlockState original, /* Method parameters */ final BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();

        if (Utils.isValidBlock(original) && original.hasProperty(Utils.QUALITY_STATE)) {
            return original.setValue(Utils.QUALITY_STATE, QualityUtils.getPlacementQuality(itemInHand));
        }

        return original;
    }
}
