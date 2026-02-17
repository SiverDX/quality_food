package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.BlockHelper;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public abstract class HarvesterMovementBehaviourMixin {
    @Unique private Quality quality_food$quality = Quality.NONE;
    @Unique private BlockPos quality_food$position;

    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    private void quality_food$storeData(final MovementContext context, final BlockPos position, final CallbackInfo callback) {
        quality_food$position = position;
        quality_food$quality = LevelData.get(context.world, position, true);
    }

    @ModifyVariable(method = "lambda$visitNewPosition$0", at = @At("HEAD"), argsOnly = true)
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final BlockState state, @Local(argsOnly = true) final MovementContext context) {
        QualityUtils.applyQuality(stack, state, quality_food$quality, null, context.world.getBlockState(quality_food$position), context.world.registryAccess());
        return stack;
    }

    /** The previous {@link BlockHelper#destroyBlockAs(Level, BlockPos, Player, ItemStack, float, Consumer)} removes the block and therefor the quality */
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", shift = At.Shift.AFTER, remap = true))
    private void quality_food$retainQuality(final MovementContext context, final BlockPos position, final CallbackInfo callback, @Local(name = "state") final BlockState cutCrop) {
        if (cutCrop.isAir()) {
            return;
        }

        LevelData.set(context.world, quality_food$position, quality_food$quality);
    }
}
