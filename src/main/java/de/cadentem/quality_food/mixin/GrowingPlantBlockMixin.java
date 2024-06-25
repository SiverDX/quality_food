package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Apply quality from item to block when placed */
@Mixin(GrowingPlantBlock.class)
public abstract class GrowingPlantBlockMixin {
    @ModifyReturnValue(method = "getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"))
    private BlockState quality_food$setQualityState(final BlockState original, /* Method parameters: */ final BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();

        if (original.hasProperty(Utils.QUALITY_STATE)) {
            return original.setValue(Utils.QUALITY_STATE, QualityUtils.getPlacementQuality(itemInHand));
        }

        return original;
    }
}
