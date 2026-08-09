package de.cadentem.quality_food.util;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.config.ServerConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class StorageRecipeCache {
    /**
     * @param size     The number of items the storage-block will contain
     * @param packing  Which way the conversion is handled (into storage-block or into items)
     */
    public record Entry(int size, boolean packing) {}

    private static @Nullable Map<ResourceLocation, Entry> cache;

    private record Candidate(ResourceLocation id, Set<Item> inputs, Item output, int size) {}

    public static @Nullable Entry get(@Nullable final Recipe<?> recipe, final Level level) {
        if (recipe == null) {
            return null;
        }

        Map<ResourceLocation, Entry> entries = cache;

        if (entries == null) {
            entries = build(level.getRecipeManager(), level.registryAccess());
            cache = entries;
        }

        return entries.get(recipe.getId());
    }

    public static boolean isStorageRecipe(@Nullable final Recipe<?> recipe, final Level level) {
        return get(recipe, level) != null;
    }

    public static void invalidate() {
        cache = null;
    }

    /** Server-side (re)load of the datapacks - the listener is appended after the vanilla ones, meaning the recipes are already parsed */
    @SubscribeEvent
    public static void invalidateOnReload(final AddReloadListenerEvent event) {
        event.addListener((ResourceManagerReloadListener) (final ResourceManager ignored) -> invalidate());
    }

    @SubscribeEvent
    public static void invalidateOnServerStop(final ServerStoppedEvent event) {
        invalidate();
    }

    private static Map<ResourceLocation, Entry> build(final RecipeManager manager, final RegistryAccess access) {
        List<Candidate> packing = new ArrayList<>();
        // Index by input item to avoid comparing every packing recipe against every unpacking recipe
        Map<Item, List<Candidate>> unpackingByInput = new HashMap<>();

        for (Recipe<?> recipe : manager.getRecipes()) {
            if (recipe.isSpecial()) {
                continue;
            }

            ItemStack result;

            try {
                result = recipe.getResultItem(access);
            } catch (Exception ignored) {
                // Certain (modded) recipe types don't support being queried outside a crafting context
                continue;
            }

            if (result.isEmpty() || result.hasTag()) {
                // A storage block should not carry any data
                continue;
            }

            List<Ingredient> ingredients = new ArrayList<>();

            for (Ingredient ingredient : recipe.getIngredients()) {
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }

            if (ingredients.isEmpty()) {
                continue;
            }

            if (result.getCount() == 1 && ingredients.size() > 1) {
                Set<Item> inputs = toItems(ingredients.get(0));

                if (isInvalidSide(inputs, result.getItem())) {
                    continue;
                }

                // All ingredients must be the same item (or the same set of items in case of a tag)
                for (int i = 1; i < ingredients.size(); i++) {
                    if (!inputs.equals(toItems(ingredients.get(i)))) {
                        inputs = null;
                        break;
                    }
                }

                if (inputs != null) {
                    packing.add(new Candidate(recipe.getId(), inputs, result.getItem(), ingredients.size()));
                }
            } else if (result.getCount() > 1 && ingredients.size() == 1) {
                Set<Item> inputs = toItems(ingredients.get(0));

                if (isInvalidSide(inputs, result.getItem())) {
                    continue;
                }

                for (Item input : inputs) {
                    unpackingByInput.computeIfAbsent(input, key -> new ArrayList<>()).add(new Candidate(recipe.getId(), inputs, result.getItem(), result.getCount()));
                }
            }
        }

        Map<ResourceLocation, Entry> detected = new HashMap<>();

        for (Candidate pack : packing) {
            if (!isRelevant(pack)) {
                continue;
            }

            for (Candidate unpack : unpackingByInput.getOrDefault(pack.output(), Collections.emptyList())) {
                if (pack.size() != unpack.size()) {
                    // Not count-preserving - either items are lost or duplicated
                    continue;
                }

                if (!pack.inputs().contains(unpack.output())) {
                    // The item must come back as the same item it went in as
                    continue;
                }

                if (isAllowed(pack.id())) {
                    detected.put(pack.id(), new Entry(pack.size(), true));
                }

                if (isAllowed(unpack.id())) {
                    detected.put(unpack.id(), new Entry(unpack.size(), false));
                }
            }
        }

        QualityFood.LOG.info("Detected {} storage block recipes", detected.size());

        if (QualityFood.LOG.isDebugEnabled()) {
            detected.forEach((id, entry) -> QualityFood.LOG.debug("- {} ({}, size {})", id, entry.packing() ? "packing" : "unpacking", entry.size()));
        }

        return detected;
    }

    /** Only relevant if quality can be applied to either side of the round trip (avoids keeping e.g. iron ingot / iron block around) */
    private static boolean isRelevant(final Candidate pack) {
        if (Utils.isValidItem(pack.output().getDefaultInstance())) {
            return true;
        }

        for (Item input : pack.inputs()) {
            if (Utils.isValidItem(input.getDefaultInstance())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isInvalidSide(final Set<Item> inputs, final Item output) {
        if (inputs.isEmpty() || inputs.contains(output)) {
            // No self-loops
            return true;
        }

        for (Item input : inputs) {
            //noinspection deprecation -> ignore
            if (input.hasCraftingRemainingItem()) {
                // Recipes with a remainder (bucket, bottle, ...) are not a plain conversion
                return true;
            }
        }

        return false;
    }

    private static boolean isAllowed(final ResourceLocation id) {
        return !ServerConfig.STORAGE_RECIPE_BLACKLIST.get().contains(id.toString());
    }

    private static Set<Item> toItems(final Ingredient ingredient) {
        return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }
}
