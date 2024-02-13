package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @ModifyVariable(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack quality_food$applyQuality(final ItemStack stack, /* Method arguments: */ final Level level) {
        if (stack.is(Items.GLOW_BERRIES)) {
            QualityUtils.applyQuality(stack, level);
        }

        return stack;
    }

    @Inject(method = "createBlockStateDefinition", at = @At(value = "TAIL"))
    private void quality_food$addQualityProperty(final StateDefinition.Builder<Block, BlockState> builder, final CallbackInfo callback) {
        if (Utils.isValidBlock((Block) (Object) this)) {
            builder.add(Utils.QUALITY_STATE);
        }
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState quality_food$setQualityState(final BlockState original, /* Method parameters: */ BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();

        if (Utils.isValidBlock(original) && original.hasProperty(Utils.QUALITY_STATE)) {
            return original.setValue(Utils.QUALITY_STATE, QualityUtils.getQuality(itemInHand).ordinal());
        }

        return original;
    }
}
