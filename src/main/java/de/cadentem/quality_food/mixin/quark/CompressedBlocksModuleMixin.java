package de.cadentem.quality_food.mixin.quark;

//import de.cadentem.quality_food.compat.PropertiesExtension;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//import org.violetmoon.quark.content.building.module.CompressedBlocksModule;
//import org.violetmoon.zeta.module.ZetaModule;
//
///** To identify the blocks which need the quality state */
//@Mixin(value = CompressedBlocksModule.class, remap = false)
//public abstract class CompressedBlocksModuleMixin {
//    @ModifyArg(method = "crate", at = @At(value = "INVOKE", target = "Lorg/violetmoon/zeta/block/ZetaFlammableBlock;<init>(Ljava/lang/String;Lorg/violetmoon/zeta/module/ZetaModule;ILnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
//    private BlockBehaviour.Properties quality_food$handleCrate(final BlockBehaviour.Properties properties) {
//        return ((PropertiesExtension) properties).quality_food$qualityBlock();
//    }
//
//    @ModifyArg(method = "sack(Ljava/lang/String;Lnet/minecraft/world/level/material/MapColor;IZLjava/util/function/BooleanSupplier;)Lnet/minecraft/world/level/block/Block;", at = @At(value = "INVOKE", target = "Lorg/violetmoon/zeta/block/ZetaFlammableBlock;<init>(Ljava/lang/String;Lorg/violetmoon/zeta/module/ZetaModule;ILnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
//    private BlockBehaviour.Properties quality_food$handleSack(final String registryName, final ZetaModule module, int flammability, final BlockBehaviour.Properties properties) {
//        if (registryName.equals("cocoa_beans_sack") || registryName.equals("berry_sack") || registryName.equals("glowberry_sack")) {
//            return ((PropertiesExtension) properties).quality_food$qualityBlock();
//        }
//
//        return properties;
//    }
//
//    @ModifyArg(method = "pillar", at = @At(value = "INVOKE", target = "Lorg/violetmoon/zeta/block/ZetaFlammablePillarBlock;<init>(Ljava/lang/String;Lorg/violetmoon/zeta/module/ZetaModule;ILnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
//    private BlockBehaviour.Properties quality_food$handlePillar(final String registryName, final ZetaModule module, int flammability, final BlockBehaviour.Properties properties) {
//        if (registryName.equals("sugar_cane_block") || registryName.equals("chorus_fruit_block")) {
//            return ((PropertiesExtension) properties).quality_food$qualityBlock();
//        }
//
//        return properties;
//    }
//}
