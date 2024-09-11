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
        ExistingFileHelper helper = event.getExistingFileHelper();

        QFBlockTags blockTags = new QFBlockTags(generator.getPackOutput(), event.getLookupProvider(), helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new QFItemTags(generator.getPackOutput(), event.getLookupProvider(), blockTags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new QFLootModifiers(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new QFEffectTags(generator.getPackOutput(), event.getLookupProvider(), helper));
    }
}