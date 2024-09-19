package de.cadentem.quality_food.compat;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

import java.util.HashMap;
import java.util.Map;

public class Compat {
    public static final String APPLESKIN = "appleskin";
    public static final String QUARK = "quark";
    public static final String COMMON = "c";
    public static final String CREATE = "create";
    public static final String VINERY = "vinery";
    public static final String FARMERSDELIGHT = "farmersdelight";
    public static final String SUPPLEMENTARIES = "supplementaries";
    public static final String HARVEST_WITH_EASE = "harvestwithease";
    public static final String CRATE_DELIGHT = "cratedelight";
    public static final String FARM_AND_CHARM = "farm_and_charm";

    private static final Map<String, Boolean> MODS = new HashMap<>();

    public static boolean isModLoaded(final String mod) {
        return MODS.computeIfAbsent(mod, key -> {
            ModList modList = ModList.get();

            if (modList != null) {
                return modList.isLoaded(key);
            }

            return LoadingModList.get().getModFileById(key) != null;
        });
    }

    public static ResourceLocation quark(final String path) {
        return location(QUARK, path);
    }

    public static ResourceLocation farmersdelight(final String path) {
        return location(FARMERSDELIGHT, path);
    }

    public static ResourceLocation supplementaries(final String path) {
        return location(SUPPLEMENTARIES, path);
    }

    public static ResourceLocation vinery(final String path) {
        return location(VINERY, path);
    }

    public static ResourceLocation cratedelight(final String path) {
        return location(CRATE_DELIGHT, path);
    }

    public static ResourceLocation farmandcharm(final String path) {
        return location(FARM_AND_CHARM, path);
    }

    public static ResourceLocation common(final String path) {
        return location(COMMON, path);
    }

    public static ResourceLocation location(final String namespace, final String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
