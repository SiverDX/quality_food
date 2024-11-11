package de.cadentem.quality_food.client;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.EffectComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = QualityFood.MODID, dist = Dist.CLIENT)
public class QualityFoodClient {
    public QualityFoodClient(final IEventBus bus, final ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        
        bus.addListener(this::registerTooltips);
    }

    public void registerTooltips(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(EffectComponent.class, ClientEffectComponent::new);
    }
}
