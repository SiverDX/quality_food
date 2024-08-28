package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public abstract class HarvesterMovementBehaviourMixin {
    @Unique private BlockPos quality_food$position;

    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    private void quality_food$storeData(final MovementContext context, final BlockPos position, final CallbackInfo callback) {
        quality_food$position = position;
    }

    @ModifyVariable(method = "lambda$visitNewPosition$0", at = @At("HEAD"), argsOnly = true)
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final BlockState state, @Local(argsOnly = true) final MovementContext context) {
        QualityUtils.applyQuality(stack, state, null, context.world.getBlockState(quality_food$position));
        return stack;
    }

    @ModifyArg(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", remap = true))
    private BlockState quality_food$retainQuality(final BlockState newState, @Local(ordinal = 0) final BlockState oldState) {
        if (newState.hasProperty(Utils.QUALITY_STATE) && oldState.hasProperty(Utils.QUALITY_STATE)) {
            newState.setValue(Utils.QUALITY_STATE, oldState.getValue(Utils.QUALITY_STATE));
        }

        return newState;
    }
}
