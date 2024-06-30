package de.cadentem.quality_food.compat.quark;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.block.ZetaFlammableBlock;
import org.violetmoon.zeta.module.ZetaModule;

/** To identify the blocks which need the quality state */
public class QuarkBlock extends ZetaFlammableBlock {
    public QuarkBlock(final String registryName, @Nullable final ZetaModule module, int flammability, final Properties properties) {
        super(registryName, module, flammability, properties);
    }
}
