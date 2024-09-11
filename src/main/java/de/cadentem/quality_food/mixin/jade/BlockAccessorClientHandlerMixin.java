package de.cadentem.quality_food.mixin.jade;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.api.BlockAccessor;
import snownee.jade.impl.BlockAccessorClientHandler;

/** Jade for 1.20 does not support Block server data */
@Mixin(value = BlockAccessorClientHandler.class, remap = false)
public abstract class BlockAccessorClientHandlerMixin {
    @ModifyReturnValue(method = "shouldRequestData(Lsnownee/jade/api/BlockAccessor;)Z", at = @At("RETURN"))
    private boolean quality_food$requestQualtiyData(boolean shouldRequest, @Local(argsOnly = true) final BlockAccessor accessor) {
        if (!shouldRequest && Utils.isValidBlock(accessor.getBlockState())) {
            return true;
        }

        return shouldRequest;
    }
}
