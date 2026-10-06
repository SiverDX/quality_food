package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.util.RecipeExtension;
import de.cadentem.quality_food.util.StorageRecipeCache;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static de.cadentem.quality_food.compat.Compat.*;
import static de.cadentem.quality_food.compat.Compat.Mod.*;

@EventBusSubscriber
public class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final List<FarmlandConfig> FARMLAND_CONFIG = new ArrayList<>();

    public static final ModConfigSpec.DoubleValue LUCK_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CROP_TARGET_CHANCE;
    public static final ModConfigSpec.DoubleValue SEED_CHANCE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue HANDLE_SEED_RECIPES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> STORAGE_RECIPE_BLACKLIST;
    public static final ModConfigSpec.DoubleValue CHILD_POTENTIAL_BASE_PERCENTAGE;
    public static final ModConfigSpec.DoubleValue CHILD_POTENTIAL_RANDOM_FACTOR_AMOUNT;
    public static final ModConfigSpec.DoubleValue CHILD_POTENTIAL_GROWTH_MULTIPLIER;
    public static final ModConfigSpec.IntValue ANIMAL_POTENTIAL_IMPACT;
    public static final ModConfigSpec.IntValue MAX_NATURAL_HARVEST_QUALITY_LEVEL;
    public static final ModConfigSpec.IntValue MAX_NATURAL_LOOT_QUALITY_LEVEL;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
    private static final List<String> NO_QUALITY_RECIPES_DEFAULT = new ArrayList<>();
    private static final List<String> RETAIN_QUALITY_RECIPES_DEFAULT = new ArrayList<>();

    private static MinecraftServer server;

    static {
        fillRetainQualityRecipes();

        LUCK_MULTIPLIER = BUILDER.comment("Luck will affect how often each quality will be tried for (10 luck * 0.25 multiplier -> 2.5 rolls, meaning 2 rolls and 50% chance for another)").defineInRange("luck_multiplier", 0.25d, 0f, 10);
        String cropTargetChanceComment = "The chance of quality crops dropping it's own quality (also affects other qualities)\nExample for Gold (20 -> 0.6 / 0.03) the chances for all qualities would then be: 20 * 0.10 (iron) = 2 (100%) | 20 * 0.03 (gold) = 0.6 (60%) | 20 * 0.005 = 0.1 (10%)";
        CROP_TARGET_CHANCE = BUILDER.comment(cropTargetChanceComment).defineInRange("crop_target_chance", 0.6d, 0, 1);
        SEED_CHANCE_MULTIPLIER = BUILDER.comment("Multiplier on top of the crop target chance").defineInRange("seed_chance_multiplier", 0.25, 0, 100);
        String farmlandConfigComment = "Define multipliers to be applied per farmland on crops - Syntax: <index>;<crop>;<farmland>;<multiplier>\n(the index defines the sequence in which they will be checked - the first matching one is applied)";
        FARMLAND_CONFIG_INTERNAL = BUILDER.comment(farmlandConfigComment).defineList("farmland_config", Collections::emptyList, () -> "<index>;<crop>;<farmland>;<multiplier>", ServerConfig::validateFarmlandConfig);

        BUILDER.push("Crafting");
        String noQualityRecipesComment1 = "Define recipes (namespace:path) which should not result in quality being rolled (e.g. when the items can be converted back and forth)";
        String noQualityRecipesComment2 = "\nThis is not about keeping / retaining quality as a whole (e.g., in the case of storage-blocks - that is handled automatically + through the 'retain_quality_recipes' configuration)";
        NO_QUALITY_RECIPES = BUILDER.comment(noQualityRecipesComment1 + noQualityRecipesComment2).defineList("no_quality_recipes", () -> NO_QUALITY_RECIPES_DEFAULT, () -> "<namespace>:<path>", ServerConfig::validateRecipe);
        RETAIN_QUALITY_RECIPES = BUILDER.comment("Define recipes (namespace:path) which should result in the quality being applied to the result (only if all ingredients have the same quality)").defineList("retain_quality_recipes", () -> RETAIN_QUALITY_RECIPES_DEFAULT, () -> "<namespace>:<path>", ServerConfig::validateRecipe);
        HANDLE_SEED_RECIPES = BUILDER.comment("Attempt to handle recipes involving seed items automatically (to avoid having to add all of them to the retain_quality_recipes config)").define("handle_seed_recipes", true);
        STORAGE_RECIPE_BLACKLIST = BUILDER.comment("Define recipes (namespace:path) which should be excluded from the automatic storage block detection").defineList("storage_recipe_blacklist", Collections::emptyList, () -> "<namespace>:<path>", ServerConfig::validateRecipe);
        BUILDER.pop();

        BUILDER.push("Animals");
        CHILD_POTENTIAL_BASE_PERCENTAGE = BUILDER.comment("The base percentage of the (averaged) potential of the parents which gets passed on to the child").defineInRange("child_potential_base_percentage", 0.6, 0, 1);
        CHILD_POTENTIAL_RANDOM_FACTOR_AMOUNT = BUILDER.comment("A random value between 0 and this amount is added to the base percentage").defineInRange("child_potential_random_factor_amount", 0.3, 0, 1);
        CHILD_POTENTIAL_GROWTH_MULTIPLIER = BUILDER.comment("Multiplier on the potential gained through feeding a child (usually children can be fed more often)").defineInRange("child_potential_growth_multiplier", 0.2, 0, 1);
        ANIMAL_POTENTIAL_IMPACT = BUILDER.comment("Defines how much the potential (0 - 1) of an animal increases the odds for quality (e.g. at 49 a potential of 1 results in a 50 times higher odds)").defineInRange("animal_potential_impact", 49, 0, 1000);
        BUILDER.pop();

        BUILDER.push("Balance");
        MAX_NATURAL_HARVEST_QUALITY_LEVEL = BUILDER.comment("The maximum quality level naturally generated crops (i.e. not planted by a player) can drop").defineInRange("max_natural_harvest_quality_level", 3, 0, Integer.MAX_VALUE);
        MAX_NATURAL_LOOT_QUALITY_LEVEL = BUILDER.comment("The maximum quality level naturally generated loot (e.g. chest loot) can have").defineInRange("max_natural_loot_quality_level", 3, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void reloadConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && /* Cannot be the case when stopping the server? */ SPEC.isLoaded()) {
            FARMLAND_CONFIG.clear();
            FARMLAND_CONFIG_INTERNAL.get().forEach(entry -> FARMLAND_CONFIG.add(new FarmlandConfig(entry)));
            FARMLAND_CONFIG.sort(Comparator.comparingInt(entry -> entry.index));

            QualityFood.LOG.info("Reloaded configuration");
            QualityFood.LOG.info("- Farmland config: {}", FARMLAND_CONFIG);

            if (server != null) {
                server.getRecipeManager().getRecipes().forEach(recipe -> {
                    RecipeExtension extension = (RecipeExtension) (Object) recipe;
                    extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.NOT_INITIALIZED);
                });
            }

            // The storage recipe blacklist may have changed
            StorageRecipeCache.invalidate();
        }
    }

    public static void storeServer(final ServerStartedEvent event) {
        server = event.getServer();
    }

    public static boolean isNoQualityRecipe(@Nullable final RecipeHolder<?> recipe, final RegistryAccess access) {
        if (recipe == null) {
            return false;
        }

        if (StorageRecipeCache.isStorageRecipe(recipe)) {
            return true;
        }

        RecipeExtension.QualityFoodStatus status = getRecipeStatus(recipe, access);
        return status == RecipeExtension.QualityFoodStatus.NO_QUALITY || status == RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY;
    }

    public static boolean isRetainQualityRecipe(@Nullable final RecipeHolder<?> recipe, @Nullable RegistryAccess access) {
        if (recipe == null) {
            return false;
        }

        if (ServerConfig.HANDLE_SEED_RECIPES.get()) {
            if (access == null && ServerLifecycleHooks.getCurrentServer() != null) {
                access = ServerLifecycleHooks.getCurrentServer().registryAccess();
            }
        }

        RecipeExtension.QualityFoodStatus status = getRecipeStatus(recipe, access);
        return status == RecipeExtension.QualityFoodStatus.RETAIN_QUALITY || status == RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY;
    }

    private static RecipeExtension.QualityFoodStatus getRecipeStatus(@NotNull final RecipeHolder<?> recipe, @Nullable final RegistryAccess access) {
        RecipeExtension extension = (RecipeExtension) (Object) recipe;

        if (extension.quality_food$getStatus() == RecipeExtension.QualityFoodStatus.NOT_INITIALIZED) {
            boolean isNoQualityRecipe = NO_QUALITY_RECIPES.get().contains(recipe.id().toString());
            boolean isRetainQualityRecipe = RETAIN_QUALITY_RECIPES.get().contains(recipe.id().toString());

            if (!isRetainQualityRecipe && access != null) {
                isRetainQualityRecipe = recipe.value().getResultItem(access).is(Tags.Items.SEEDS);
            }

            if (isNoQualityRecipe && isRetainQualityRecipe) {
                extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY);
            } else if (isNoQualityRecipe) {
                extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.NO_QUALITY);
            } else if (isRetainQualityRecipe) {
                extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.RETAIN_QUALITY);
            } else {
                extension.quality_food$setStatus(null);
            }
        }

        return extension.quality_food$getStatus();
    }

    public static double getFarmlandMultiplier(final BlockState crop, final BlockState farmland) {
        if (crop != null && farmland != null) {
            for (FarmlandConfig farmlandConfig : FARMLAND_CONFIG) {
                if (farmlandConfig.predicate.test(crop, farmland)) {
                    return farmlandConfig.multiplier;
                }
            }
        }

        return 1;
    }

    private static boolean validateRecipe(final Object object) {
        if (object instanceof String string) {
            return ResourceLocation.tryParse(string) != null;
        }

        return false;
    }

    private static boolean validateFarmlandConfig(final Object object) {
        if (object instanceof String string) {
            String[] data = string.split(";");

            if (data.length != 4) {
                return false;
            }

            if (isInvalidInteger(data[FarmlandConfig.INDEX])) {
                return false;
            }

            String crop = data[FarmlandConfig.CROP];

            if (ResourceLocation.tryParse(crop.startsWith("#") ? crop.substring(1) : crop) == null) {
                return false;
            }

            String farmland = data[FarmlandConfig.FARMLAND];

            if (ResourceLocation.tryParse(farmland.startsWith("#") ? farmland.substring(1) : farmland) == null) {
                return false;
            }

            try {
                double multiplier = Double.parseDouble(data[FarmlandConfig.MULTIPLIER]);

                if (multiplier < 0) {
                    return false;
                }
            } catch (NumberFormatException ignored) {
                return false;
            }

            return true;
        }

        return false;
    }

    private static boolean isInvalidInteger(final String value) {
        if (value == null) {
            return true;
        }

        if (value.contains(".") || value.contains(",")) {
            return true;
        }

        try {
            return Integer.parseInt(value) < 0;
        } catch (NumberFormatException ignored) { /* Nothing to do */ }

        return true;
    }

    private static void fillRetainQualityRecipes() {
        // Glass bottle in recipe
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_sugar_cane");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_honey_bottle");
        // Not detected automatically since the glass bottle is an additional ingredient (and a crafting remainder) - meaning it's not a plain conversion
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:honey_bottle");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:honey_block");
        // Not detected automatically since there is no recipe to turn the melon block back into melon slices
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:melon");

        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_pumpkins").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "pumpkins").toString());
        // Still Crate Delight
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_pumpkins").toString());

        // Other
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(VINTAGEDELIGHT.modid(), "honey_jar_deconstruct").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(VINTAGEDELIGHT.modid(), "honey_jar").toString());

        // 2x3 storage block
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leaf_crate").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leafs_from_crate").toString());
        // Mixed input items
//        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "mixed_tea_leaf_block").toString());
    }
}
