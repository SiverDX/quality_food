package de.cadentem.quality_food.component;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class QFRegistries {
    public static final ResourceKey<Registry<QualityType>> QUALITY_TYPE_REGISTRY = ResourceKey.createRegistryKey(QualityFood.location("quality_types"));
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(QualityFood.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Quality>> QUALITY_DATA_COMPONENT = REGISTRAR.registerComponentType(QualityFood.MODID + ".quality", builder -> builder.persistent(Quality.CODEC));

    public static RegistrySetBuilder bootstrap() {
        return new RegistrySetBuilder()
                .add(QUALITY_TYPE_REGISTRY, bootstrap -> {
                    bootstrap.register(ResourceKey.create(QUALITY_TYPE_REGISTRY, QualityFood.location("iron")), new QualityType(1, 0.1, 1.5, 1.25, 1, 1.5, 1.25, 0.15, Optional.empty(), QualityFood.location("textures/icon/iron.png")));
                    bootstrap.register(ResourceKey.create(QUALITY_TYPE_REGISTRY, QualityFood.location("gold")), new QualityType(2, 0.03, 2, 1.5, 2, 2, 1.5, 0.4, Optional.empty(), QualityFood.location("textures/icon/gold.png")));
                    bootstrap.register(ResourceKey.create(QUALITY_TYPE_REGISTRY, QualityFood.location("diamond")), new QualityType(3, 0.005, 2.5, 2, 3, 2.5, 1.75, 0.7, Optional.empty(), QualityFood.location("textures/icon/diamond.png")));
                });
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(QUALITY_TYPE_REGISTRY, QualityType.CODEC, QualityType.CODEC);
    }
}
