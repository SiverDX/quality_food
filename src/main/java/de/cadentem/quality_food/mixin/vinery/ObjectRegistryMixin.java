package de.cadentem.quality_food.mixin.vinery;

import de.cadentem.quality_food.compat.PropertiesExtension;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import satisfyu.vinery.registry.ObjectRegistry;

/** To identify the blocks which need the quality state */
@Mixin(value = ObjectRegistry.class, remap = false)
public abstract class ObjectRegistryMixin {
    @ModifyArg(method = {/* White grape */ "lambda$static$36", /* Red grape */ "lambda$static$38", /* Cherry */ "lambda$static$40", /* Apple */ "lambda$static$42"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static BlockBehaviour.Properties quality_food$handleBag(final BlockBehaviour.Properties properties) {
        return ((PropertiesExtension) properties).quality_food$qualityBlock();
    }
}
