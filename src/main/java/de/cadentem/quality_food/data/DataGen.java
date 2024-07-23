package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.component.QFRegistries;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void configureDataGen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new QFItemTags(generator.getPackOutput(), event.getLookupProvider(), helper));
        generator.addProvider(event.includeServer(), new QFLootModifiers(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new QFEffectTags(generator.getPackOutput(), event.getLookupProvider(), helper));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(), event.getLookupProvider(), QFRegistries.bootstrap(), Set.of(QualityFood.MODID)));
    }
}