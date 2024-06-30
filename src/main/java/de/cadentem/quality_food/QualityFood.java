package de.cadentem.quality_food;

import com.mojang.logging.LogUtils;
import de.cadentem.quality_food.capability.BlockData;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.compat.create.QFItemAttributes;
import de.cadentem.quality_food.events.ModEvents;
import de.cadentem.quality_food.network.NetworkHandler;
import de.cadentem.quality_food.registry.QFCommands;
import de.cadentem.quality_food.registry.QFItems;
import de.cadentem.quality_food.registry.QFLootModifiers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(QualityFood.MODID)
public class QualityFood {
    public static final String MODID = "quality_food";
    public static final Logger LOG = LogUtils.getLogger();

    public QualityFood() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        QFItems.ITEMS.register(modEventBus);
        QFLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        QFCommands.COMMAND_ARGUMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(QFCommands::registerCommands);

        if (Compat.isModLoaded(Compat.HARVEST_WITH_EASE)) {
            MinecraftForge.EVENT_BUS.addListener(ModEvents::handleHarvestEvent);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();

        if (Compat.isModLoaded(Compat.CREATE)) {
            event.enqueueWork(QFItemAttributes::register);
        }
    }

    @SubscribeEvent
    public void registerCapability(final RegisterCapabilitiesEvent event) {
        event.register(BlockData.class);
    }
}
