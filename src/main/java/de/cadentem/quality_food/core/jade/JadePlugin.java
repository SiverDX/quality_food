package de.cadentem.quality_food.core.jade;

import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.world.level.block.Block;

@WailaPlugin
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class JadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.registerComponentProvider(new QualityProvider(), TooltipPosition.BODY, Block.class);
    }
}
