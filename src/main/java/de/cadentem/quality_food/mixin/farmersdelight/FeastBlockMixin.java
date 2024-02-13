package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.FeastBlock;

@Mixin(FeastBlock.class)
public class FeastBlockMixin {
    @Inject(method = "createBlockStateDefinition", at = @At(value = "TAIL"))
    private void quality_food$addQualityProperty(final StateDefinition.Builder<Block, BlockState> builder, final CallbackInfo callback) {
        builder.add(Utils.QUALITY_STATE);
    }

    @ModifyReturnValue(method = "getServingItem", at = @At("RETURN"), remap = false)
    private ItemStack quality_food$applyQualityToItem(final ItemStack stack, /* Method parameters: */ final BlockState state) {
        QualityUtils.applyQuality(stack, Quality.get(state.getValue(Utils.QUALITY_STATE)));
        return stack;
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState quality_food$setQualityState(final BlockState original, /* Method parameters: */ final BlockPlaceContext context) {
        return original.setValue(Utils.QUALITY_STATE, QualityUtils.getQuality(context.getItemInHand()).ordinal());
    }
}
