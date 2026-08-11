package de.cadentem.quality_food.compat.jade;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
@SuppressWarnings("unused")
public class JadePlugin implements IWailaPlugin {
    @Override
    public void register(final IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(new QualityMobProvider(), Mob.class);
    }

    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.registerBlockComponent(new QualityBlockProvider(), Block.class);
        registration.registerEntityComponent(new QualityMobProvider(), Mob.class);
    }
}
