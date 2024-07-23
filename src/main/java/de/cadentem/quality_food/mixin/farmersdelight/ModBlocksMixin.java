package de.cadentem.quality_food.mixin.farmersdelight;

//import de.cadentem.quality_food.compat.PropertiesExtension;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//import vectorwing.farmersdelight.common.registry.ModBlocks;
//
///** To identify the blocks which need the quality state */
//@Mixin(value = ModBlocks.class, remap = false)
//public abstract class ModBlocksMixin {
//    @ModifyArg(method = {/* Carrot */ "lambda$static$6", /* Potato */ "lambda$static$7", /* Beetroot */ "lambda$static$8", /* Cabbage */ "lambda$static$9", /* Tomato */ "lambda$static$10", /* Onion */ "lambda$static$11", /* Rice bag */ "lambda$static$13"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
//    private static BlockBehaviour.Properties quality_food$handleCrates(final BlockBehaviour.Properties properties) {
//        return ((PropertiesExtension) properties).quality_food$qualityBlock();
//    }
//
//    @ModifyArg(method = "lambda$static$12", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/RiceBaleBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
//    private static BlockBehaviour.Properties quality_food$handleRiceBale(final BlockBehaviour.Properties properties) {
//        return ((PropertiesExtension) properties).quality_food$qualityBlock();
//    }
//}
