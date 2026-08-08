package de.cadentem.quality_food.compat;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Separate class to make sure we're not loading any unnecessary classes when mixins are being initialized */
public class Compat {
    private static final Map<String, List<String>> ALIAS = Map.of();
    private static final Map<String, Boolean> MODS = new HashMap<>();

    public static ResourceLocation common(final String path) {
        return location("c", path);
    }

    public static ResourceLocation location(final String namespace, final String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private static boolean isModLoaded(final String mod) {
        return MODS.computeIfAbsent(mod, key -> {
            if (check(key)) {
                return true;
            }

            for (String alias : ALIAS.getOrDefault(key, List.of())) {
                if (check(alias)) {
                    return true;
                }
            }

            return false;
        });
    }

    private static boolean check(final String modid) {
        ModList modList = ModList.get();

        if (modList != null && modList.isLoaded(modid)) {
            return true;
        }

        return LoadingModList.get().getModFileById(modid) != null;
    }

    public enum Mod {
        QUARK("quark"),
        CREATE("create"),
        VINERY("vinery"),
        FARMERSDELIGHT("farmersdelight"),
        SUPPLEMENTARIES("supplementaries"),
        HARVEST_WITH_EASE("harvestwithease"),
        FRUITFUL_FUN("fruitfulfun"),
        CRATE_DELIGHT("cratedelight"),
        COLLECTORS_REAP("collectorsreap"),
        FARM_AND_CHARM("farm_and_charm"),
        MINERS_DELIGHT("miners_delight"),
        TOOLTIPOVERHAUL("tooltipoverhaul"),
        HERALBREWS("herbalbrews"),
        KALEIDOSCOPE_COOKERY("kaleidoscope_cookery"),
        VINTAGEDELIGHT("vintagedelight");;

        private final String modid;

        Mod(final String modid) {
            this.modid = modid;
        }

        public String modid() {
            return modid;
        }

        public boolean isLoaded() {
            return Compat.isModLoaded(modid);
        }
    }
}