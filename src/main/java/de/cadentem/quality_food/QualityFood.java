package de.cadentem.quality_food;

import com.mojang.logging.LogUtils;
import de.cadentem.quality_food.registry.QFItems;
import de.cadentem.quality_food.registry.QFLootModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(QualityFood.MODID)
public class QualityFood {
    public static final String MODID = "quality_food";
    private static final Logger LOG = LogUtils.getLogger();
    private static final Map<String, Boolean> MODS = new HashMap<>();

    public QualityFood() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        QFItems.ITEMS.register(modEventBus);
        QFLootModifiers.LOOT_MODIFIERS.register(modEventBus);
    }

    public static boolean isModLoaded(final String mod) {
        return MODS.computeIfAbsent(mod, key -> ModList.get().isLoaded(mod));
    }

    public static final String FARMERSDELIGHT = "farmersdelight";
}
