package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public abstract class HarvesterMovementBehaviourMixin {
    /** Re-apply the harvested quality (the block gets destroyed and therefor the quality removed before being re-set to its initial age) */
    @ModifyArg(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", remap = true))
    private BlockState quality_food$retainQuality(final BlockState state, @Local(argsOnly = true) final BlockPos position, @Local(name = "world") final Level level, @Local(name = "seedSubtracted") final MutableBoolean seedSubtracted) {
        if (state.isAir() || !seedSubtracted.booleanValue()) {
            return state;
        }

        Quality quality = LevelData.get(level, position, true);

        if (quality == Quality.NONE) {
            return state;
        }

        LevelData.set(level, position, quality);
        return state;
    }
}
