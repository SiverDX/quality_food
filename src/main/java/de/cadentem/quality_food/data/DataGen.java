package de.cadentem.quality_food.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void configureDataGen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new QFItemTags(generator.getPackOutput(), event.getLookupProvider(), fileHelper));
        generator.addProvider(event.includeServer(), new QFLootModifiers(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new QFEffectTags(generator.getPackOutput(), event.getLookupProvider(), fileHelper));
    }
}