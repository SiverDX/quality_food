package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Support for quality block state */
@Mixin(StateDefinition.Builder.class)
public abstract class StateDefinitionMixin<O, S extends StateHolder<O, S>> {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void quality_food$addQualityState(final O owner, final CallbackInfo callback) {
        if (owner instanceof Block block && Utils.isValidBlock(block)) {
            add(Utils.QUALITY_STATE);
        }
    }

    @Shadow public abstract StateDefinition.Builder<O, S> add(final Property<?>... properties);
}
