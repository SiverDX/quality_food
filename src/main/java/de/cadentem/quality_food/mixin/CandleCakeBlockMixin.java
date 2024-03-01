package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Add support for the quality block state */
@Mixin(CandleCakeBlock.class)
public class CandleCakeBlockMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CakeBlock;eat(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;"))
    private BlockState quality_food$applyQualityState(final BlockState defaultState, @Local(argsOnly = true) final BlockState state) {
        return defaultState.setValue(Utils.QUALITY_STATE, state.getValue(Utils.QUALITY_STATE));
    }
}
