package de.cadentem.quality_food.mixin.quark;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.compat.PropertiesExtension;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vazkii.quark.content.building.module.CompressedBlocksModule;

/** To identify the blocks which need the quality state */
@Mixin(value = CompressedBlocksModule.class, remap = false)
public abstract class CompressedBlocksModuleMixin {
    @ModifyArg(method = "crate", at = @At(value = "INVOKE", target = "Lvazkii/quark/base/block/QuarkFlammableBlock;<init>(Ljava/lang/String;Lvazkii/quark/base/module/QuarkModule;Lnet/minecraft/world/item/CreativeModeTab;ILnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private BlockBehaviour.Properties quality_food$handleCrate(final BlockBehaviour.Properties properties) {
        return ((PropertiesExtension) properties).quality_food$qualityBlock();
    }

    @ModifyArg(method = "sack(Ljava/lang/String;Lnet/minecraft/world/level/material/MaterialColor;IZLjava/util/function/BooleanSupplier;)Lnet/minecraft/world/level/block/Block;", at = @At(value = "INVOKE", target = "Lvazkii/quark/base/block/QuarkFlammableBlock;<init>(Ljava/lang/String;Lvazkii/quark/base/module/QuarkModule;Lnet/minecraft/world/item/CreativeModeTab;ILnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private BlockBehaviour.Properties quality_food$handleSack(final BlockBehaviour.Properties properties, @Local(argsOnly = true) final String registryName) {
        if (registryName.equals("cocoa_beans_sack") || registryName.equals("berry_sack") || registryName.equals("glowberry_sack")) {
            return ((PropertiesExtension) properties).quality_food$qualityBlock();
        }

        return properties;
    }

    @ModifyArg(method = "pillar", at = @At(value = "INVOKE", target = "Lvazkii/quark/base/block/QuarkFlammablePillarBlock;<init>(Ljava/lang/String;Lvazkii/quark/base/module/QuarkModule;Lnet/minecraft/world/item/CreativeModeTab;ILnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private BlockBehaviour.Properties quality_food$handlePillar(final BlockBehaviour.Properties properties, @Local(argsOnly = true) final String registryName) {
        if (registryName.equals("sugar_cane_block") || registryName.equals("chorus_fruit_block")) {
            return ((PropertiesExtension) properties).quality_food$qualityBlock();
        }

        return properties;
    }
}
