package de.cadentem.quality_food.compat;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

import java.util.HashMap;
import java.util.Map;

public class Compat {
    public static final String QUARK = "quark";
    public static final String FORGE = "forge";
    public static final String CREATE = "create";
    public static final String VINERY = "vinery";
    public static final String FARMERSDELIGHT = "farmersdelight";
    public static final String SUPPLEMENTARIES = "supplementaries";
    public static final String HARVEST_WITH_EASE = "harvestwithease";

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

    public static ResourceLocation forge(final String path) {
        return location(FORGE, path);
    }

    private static ResourceLocation location(final String namespace, final String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
