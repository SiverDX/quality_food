package de.cadentem.quality_food.mixin.jade;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.api.BlockAccessor;
import snownee.jade.impl.BlockAccessorClientHandler;

/** Jade for 1.20 does not support Block server data */
@Mixin(value = BlockAccessorClientHandler.class, remap = false)
public abstract class BlockAccessorClientHandlerMixin {
    @ModifyReturnValue(method = "shouldRequestData(Lsnownee/jade/api/BlockAccessor;)Z", at = @At("RETURN"))
    private boolean quality_food$requestQualtiyData(boolean shouldRequest, @Local(argsOnly = true) final BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();

        if (!shouldRequest && (Utils.isValidBlock(state) || Utils.isBlockException(state))) {
            return true;
        }

        return shouldRequest;
    }
}
