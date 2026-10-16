package de.cadentem.quality_food;

import com.mojang.logging.LogUtils;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.compat.harvestwithease.ModEvents;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.registry.QFCommands;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.registry.QFLootModifiers;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(QualityFood.MODID)
public class QualityFood {
    public static final String MODID = "quality_food";
    public static final Logger LOG = LogUtils.getLogger();

    public QualityFood(final IEventBus bus, final ModContainer container) {
        QFComponents.REGISTRAR.register(bus);
        AttachmentHandler.ATTACHMENT_TYPES.register(bus);
        QFLootModifiers.LOOT_MODIFIERS.register(bus);
        QFCommands.COMMAND_ARGUMENTS.register(bus);
        NeoForge.EVENT_BUS.addListener(QFCommands::registerCommands);
        NeoForge.EVENT_BUS.addListener(ServerConfig::storeServer);

        if (Compat.Mod.HARVEST_WITH_EASE.isLoaded()) {
            NeoForge.EVENT_BUS.addListener(ModEvents::handleHarvestEvent);
        }

        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    public static Identifier location(final String path) {
        return Identifier.fromNamespaceAndPath(QualityFood.MODID, path);
    }

    public static String concat(final String path) {
        return QualityFood.MODID + "." + path;
    }
}
