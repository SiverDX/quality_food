package de.cadentem.quality_food.mixin.quark;

import de.cadentem.quality_food.compat.quark.QuarkBlock;
import de.cadentem.quality_food.compat.quark.QuarkPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.violetmoon.quark.content.building.module.CompressedBlocksModule;
import org.violetmoon.zeta.block.ZetaFlammableBlock;
import org.violetmoon.zeta.block.ZetaFlammablePillarBlock;
import org.violetmoon.zeta.module.ZetaModule;

/** To identify the blocks which need the quality state */
@Mixin(value = CompressedBlocksModule.class, remap = false)
public abstract class CompressedBlocksModuleMixin {
    @Redirect(method = "crate", at = @At(value = "NEW", args = "class=org/violetmoon/zeta/block/ZetaFlammableBlock"))
    private ZetaFlammableBlock quality_food$handleCrate(final String registryName, final ZetaModule module, int flammability, final BlockBehaviour.Properties properties) {
        return new QuarkBlock(registryName, module, flammability, properties);
    }

    @Redirect(method = "sack(Ljava/lang/String;Lnet/minecraft/world/level/material/MapColor;IZLjava/util/function/BooleanSupplier;)Lnet/minecraft/world/level/block/Block;", at = @At(value = "NEW", args = "class=org/violetmoon/zeta/block/ZetaFlammableBlock"))
    private ZetaFlammableBlock quality_food$handleSack(final String registryName, ZetaModule module, int flammability, BlockBehaviour.Properties properties) {
        if (registryName.equals("cocoa_beans_sack") || registryName.equals("berry_sack") || registryName.equals("glowberry_sack")) {
            return new QuarkBlock(registryName, module, flammability, properties);
        }

        return new ZetaFlammableBlock(registryName, module, flammability, properties);
    }

    @Redirect(method = "pillar", at = @At(value = "NEW", args = "class=org/violetmoon/zeta/block/ZetaFlammablePillarBlock"))
    private ZetaFlammablePillarBlock quality_food$handlePillar(final String registryName, ZetaModule module, int flammability, BlockBehaviour.Properties properties) {
        if (registryName.equals("sugar_cane_block") || registryName.equals("chorus_fruit_block")) {
            return new QuarkPillarBlock(registryName, module, flammability, properties);
        }

        return new ZetaFlammablePillarBlock(registryName, module, flammability, properties);
    }
}
