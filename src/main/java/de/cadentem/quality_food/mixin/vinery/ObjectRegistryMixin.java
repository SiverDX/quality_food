package de.cadentem.quality_food.mixin.vinery;

import de.cadentem.quality_food.compat.vinery.VineryBlock;
import de.cristelknight.doapi.common.block.FacingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.satisfy.vinery.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** To identify the blocks which need the quality state */
@Mixin(value = ObjectRegistry.class, remap = false)
public abstract class ObjectRegistryMixin {
    @Redirect(method = {/* White grape */ "lambda$static$31", /* Red grape */ "lambda$static$32", /* Cherry */ "lambda$static$33", /* Apple */ "lambda$static$34"}, at = @At(value = "NEW", args = "class=de/cristelknight/doapi/common/block/FacingBlock"))
    private static FacingBlock quality_food$handleBag(final BlockBehaviour.Properties properties) {
        return new VineryBlock(properties);
    }
}
