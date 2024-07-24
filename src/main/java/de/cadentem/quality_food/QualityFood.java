package de.cadentem.quality_food;

import com.mojang.logging.LogUtils;
import de.cadentem.quality_food.attachments.AttachmentHandler;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.component.QFRegistries;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.events.ModEvents;
import de.cadentem.quality_food.registry.QFCommands;
import de.cadentem.quality_food.registry.QFLootModifiers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(QualityFood.MODID)
public class QualityFood {
    public static final String MODID = "quality_food";
    public static final Logger LOG = LogUtils.getLogger();

    public QualityFood(final IEventBus bus, final ModContainer container) {
        bus.register(this);

        QFRegistries.REGISTRAR.register(bus);
        AttachmentHandler.ATTACHMENT_TYPES.register(bus);
        QFLootModifiers.LOOT_MODIFIERS.register(bus);
        QFCommands.COMMAND_ARGUMENTS.register(bus);
        NeoForge.EVENT_BUS.addListener(QFCommands::registerCommands);

        if (Compat.isModLoaded(Compat.HARVEST_WITH_EASE)) {
            NeoForge.EVENT_BUS.addListener(ModEvents::handleHarvestEvent);
        }

        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
//        if (Compat.isModLoaded(Compat.CREATE)) {
//            event.enqueueWork(QFItemAttributes::register);
//        }
    }

    public static ResourceLocation location(final String path) {
        return ResourceLocation.fromNamespaceAndPath(QualityFood.MODID, path);
    }

    public static String concat(final String path) {
        return QualityFood.MODID + "." + path;
    }
}
