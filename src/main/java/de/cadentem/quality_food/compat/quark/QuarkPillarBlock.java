package de.cadentem.quality_food.compat.quark;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.block.ZetaFlammablePillarBlock;
import org.violetmoon.zeta.module.ZetaModule;

/** To identify the blocks which need the quality state */
public class QuarkPillarBlock extends ZetaFlammablePillarBlock {
    public QuarkPillarBlock(final String registryName, @Nullable final ZetaModule module, int flammability, final Properties properties) {
        super(registryName, module, flammability, properties);
    }
}
