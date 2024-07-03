package de.cadentem.quality_food.events;

import de.cadentem.quality_food.core.loot_modifiers.QualityLootModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void onModConfigEvent(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(QualityLootModifier.SERIALIZER.setRegistryName(QualityLootModifier.ID));
    }
}
