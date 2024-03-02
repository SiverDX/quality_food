package de.cadentem.quality_food;

import com.mojang.logging.LogUtils;
import de.cadentem.quality_food.capability.BlockData;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.events.ForgeEvents;
import de.cadentem.quality_food.events.ModEvents;
import de.cadentem.quality_food.network.NetworkHandler;
import de.cadentem.quality_food.registry.QFItems;
import de.cadentem.quality_food.registry.QFLootModifiers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.LoadingModList;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(QualityFood.MODID)
public class QualityFood {
    public static final String MODID = "quality_food";
    public static final Logger LOG = LogUtils.getLogger();
    private static final Map<String, Boolean> MODS = new HashMap<>();

    public QualityFood() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        QFItems.ITEMS.register(modEventBus);
        QFLootModifiers.LOOT_MODIFIERS.register(modEventBus);

        if (isModLoaded(HARVEST_WITH_EASE)) {
            MinecraftForge.EVENT_BUS.addListener(ModEvents::handleHarvestEvent);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @SubscribeEvent
    public void registerCapability(final RegisterCapabilitiesEvent event) {
        event.register(BlockData.class);
    }

    public static boolean isModLoaded(final String mod) {
        return MODS.computeIfAbsent(mod, key -> {
            ModList modList = ModList.get();

            if (modList != null) {
                return modList.isLoaded(key);
            }

            return LoadingModList.get().getModFileById(key) != null;
        });
    }

    public static final String FARMERSDELIGHT = "farmersdelight";
    public static final String HARVEST_WITH_EASE = "harvestwithease";
}
