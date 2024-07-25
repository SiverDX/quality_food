package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.core.codecs.QualityType;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void configureDataGen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        QFBlockTags blockTags = new QFBlockTags(generator.getPackOutput(), event.getLookupProvider(), helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new QFItemTags(generator.getPackOutput(), event.getLookupProvider(), blockTags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new QFLootModifiers(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new QFEffectTags(generator.getPackOutput(), event.getLookupProvider(), helper));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(), event.getLookupProvider(), createProvider(), Set.of(QualityFood.MODID)));
    }

    private static RegistrySetBuilder createProvider() {
        return new RegistrySetBuilder()
                .add(QFComponents.QUALITY_TYPE_REGISTRY, bootstrap -> {
                    bootstrap.register(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, QualityFood.location("iron")), new QualityType(1, 0.1, 1.5, 1.25, 1, 1.5, 1.25, 0.15, 1 / 256d, Optional.empty(), QualityFood.location("quality_icon/iron")));
                    bootstrap.register(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, QualityFood.location("gold")), new QualityType(2, 0.03, 2, 1.5, 2, 2, 1.5, 0.4, 1 / 128d, Optional.empty(), QualityFood.location("quality_icon/gold")));
                    bootstrap.register(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, QualityFood.location("diamond")), new QualityType(3, 0.005, 2.5, 2, 3, 2.5, 1.75, 0.7, 1 / 64d, Optional.empty(), QualityFood.location("quality_icon/diamond")));
                });
    }
}