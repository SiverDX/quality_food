package de.cadentem.quality_food.mixin.vinery;

//import de.cadentem.quality_food.compat.PropertiesExtension;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.satisfy.vinery.registry.ObjectRegistry;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//
///** To identify the blocks which need the quality state */
//@Mixin(value = ObjectRegistry.class, remap = false)
//public abstract class ObjectRegistryMixin {
//    @ModifyArg(method = {/* White grape */ "lambda$static$31", /* Red grape */ "lambda$static$32", /* Cherry */ "lambda$static$33", /* Apple */ "lambda$static$34"}, at = @At(value = "INVOKE", target = "Lde/cristelknight/doapi/common/block/FacingBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
//    private static BlockBehaviour.Properties quality_food$handleBag(final BlockBehaviour.Properties properties) {
//        return ((PropertiesExtension) properties).quality_food$qualityBlock();
//    }
//}
