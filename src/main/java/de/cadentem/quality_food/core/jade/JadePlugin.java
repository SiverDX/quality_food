package de.cadentem.quality_food.core.jade;

import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
@SuppressWarnings("unused")
public class JadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.registerBlockComponent(new QualityProvider(), Block.class);
    }
}
