package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttachedStemBlock.class)
public abstract class AttachedStemBlockMixin {
    @ModifyReturnValue(method = "updateShape", at = @At(value = "RETURN"))
    private BlockState quality_food$keepQualityState(final BlockState state, @Local(argsOnly = true, ordinal = 0) final BlockState instance) {
        if (state.hasProperty(Utils.QUALITY_STATE)) {
            return state.setValue(Utils.QUALITY_STATE, instance.getValue(Utils.QUALITY_STATE));
        }

        return state;
    }
}
