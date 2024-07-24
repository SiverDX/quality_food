package de.cadentem.quality_food.component;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class QFRegistries {
    public static final ResourceKey<Registry<QualityType>> QUALITY_TYPE_REGISTRY = ResourceKey.createRegistryKey(QualityFood.location("quality_types"));
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(QualityFood.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Quality>> QUALITY_DATA_COMPONENT = REGISTRAR.registerComponentType("quality", builder -> builder.persistent(Quality.CODEC));

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(QUALITY_TYPE_REGISTRY, QualityType.CODEC, QualityType.CODEC);
    }

    public static ResourceKey<QualityType> key(final ResourceLocation location) {
        return ResourceKey.create(QUALITY_TYPE_REGISTRY, location);
    }
}
